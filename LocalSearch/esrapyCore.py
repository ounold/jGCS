#
#  Copyright (C) 2006 Simon Funk
#  
#  This program is free software; you can redistribute it and/or modify it under 
#  the terms of the GNU General Public License as published by the Free Software 
#  Foundation; either version 2 of the License, or (at your option) any later 
#  version.
#  
#  This program is distributed in the hope that it will be useful, but WITHOUT ANY 
#  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
#  PARTICULAR PURPOSE. See the GNU General Public License (gpl.txt) for more details.
#

import esrapyMisc
import esrapyPatterns

class Pattern:
	"""A Pattern which can be matched against a source text (i.e., used for parsing).

	Patterns can be simple, such as just a literal string which must mach exactly,
	or compound, such as a sequence of other Patterns.

	Any Pattern which takes parameters (as most do) can be created with no parameters
	and then initialized (once) later with pattern.init(params...).  This makes it
	easy to have self-referential and mutually-referential patterns.  Patterns which
	take no parameters initialize normally and do not support .init().

	See each specific subclass for details on what it does and how to use it.

	If you are writing a Pattern subclass, you need to provide the following methods:

	__init__(self)  - Supply this only if your Pattern takes no parameters.
	init(self, ...) - Supply this if it does.  This is to allow postponed initialization.

	spawn(self, parser, pos) - look for this pattern at pos using the given parser.
	                  See the Parser class below for more.

	compact(self, parsing, **flags) - Returns a compact (user-friendly) representation
	                  of the given parsing of this pattern.  Flags should be propagagted
	                  to any nested calls to compact (whether via a pattern or a match).
	"""
	EOT = None	# Must set later due to import cycles?

	def __init__(self, *a, **b):
		"If the Pattern is created with no parameters, initialization is postponed."
		self.name = None
		if a or b:
			self.init(*a, **b)

	def setName(self, name):
		self.name = name
		return self

	def match(self, source, startPos=0, stopAtFirst=True, matchToEnd=True, compact=True, debug=0):
		"""Parses <source> from <startPos> with <self>.

		If compact is True, a compact, user-friendly, pretty-printable parse tree
		   (or list thereof) is returned.  If compact is False, a Match object
		   (or list thereof) is returned.

		If stopAtFirst is True, the parser will deterministically choose the first
		   successful full parsing, leaving only a single interpretation for each
		   match.  In this case, a single match or parse tree is returned.
		If stopAtFirst is False, the parser will continue on to find all possible
		   interpretations of each match.  In this case, a list of matches (or
		   parse trees) is returned.

		If matchToEnd is True (the default), the pattern must match the source to
		   the end--i.e., with no trailing unmatched text.

		If debug is 1, more details is shown when a syntax error is encountered.
		If debug is 2, the entire parsing process is copiously elaborated.

		WARNING: the combination compact=True and stopAtFirst=False may in fact
		   return an object that's impossible to decypher consistently; I'll
		   need to think about this more... (or let me know if you do.)
		"""
		if matchToEnd:
			if not Pattern.EOT: Pattern.EOT = esrapyPatterns.Regex(r'\Z') # End of Text.
			pat = esrapyPatterns.Sequence((('_', self), Pattern.EOT))
		else:
			pat = self
		m = Parser(source, stopAtFirst).parse(pat, startPos=startPos, debug=debug)
		if compact:
			if stopAtFirst:
				return m.compact()
			else:
				return [mm.compact(all=True) for mm in m]
		return m

	def __str__(self):
		if self.name:
			return self.name
		# If we have no attributes beyond .name, assume we haven't been initialized yet
		# (Of course, this is bogus if any Patterns take no parameters and require no
		#  initialization; if that becomes a problem this can just be removed.)
		if len(vars(self)) == 1:
			return "UNINITIALIZED_PATTERN"
		return self.str()

	def getName(self):
		"Returns the pattern's unique name, or its class name if it has no unique name."
		if self.name:
			return self.name
		return self.__class__.name

class Match:
	"""A successfully parsed instance of a pattern.

	A Match object has the following fields:

	    .pattern  - the Pattern object this is a match of.

	    .start    - the begining of the pattern (position in the source text).

	    .end      - (just after) the end of the pattern.

	    .parsings - a list of possible parsings, where each parsing is in
	                turn either a plain string, or a list of matches.
	                .parsings[0] should always be valid since the match
	                should not be created unless there is at least one valid
	                parsing.

	    .attrs    - optional attributes, specific to the type of pattern.
	                This can be any hashable value (including tuples, etc.).
	                This might, for instance, be an indentation depth, or
	                an operator binding priority; or in natural language it
	                could include plurality indicators, etc.  Usually, it is None.
	"""

	def __init__(self, pattern, start, end, parsing, attrs=None):

		# The basic identity of the match:
		self.pattern = pattern
		self.start   = start
		self.end     = end
		self.attrs   = attrs	# (often wasted space..)

		# All parsings for this pattern and span
		# (typically just one, hence using a tupple rather than an array):
		self.parsings = ()
		self.addParsing(parsing)

		# A Cache of our compact representation(s):
		self.compactCache = None

	def addParsing(self, parsing):
		self.parsings += (parsing,)

	def compact(self, all=False):
		"""Returns a compact (user-friendly) representation of the match.

		What form this will take depends on the match's originating Pattern.
		However, whatever it is it should display well with the standard python
		pretty-printer, pprint().  See each Pattern subclass for more specifics.

		If all is False, a single value is returned representing the first
		valid parsing.

		If all is True, a list (or tuple) of values is returned, one for each
		possible parsing.  If the Pattern is unambiguous (with respect to the
		input) or if the parser that generated the match was invoked with
		stopAtFirst=True, the returned array will always have exactly one element.

		Additionally, if all is True, each element further down the returned
		tree will also be a list rather than a single value!  So beware,
		this flag substantially changes the structure of the entire returned
		tree."""

		if all:
			# We're caching this because it may participate in multiple parsings:
			if not self.compactCache:
				self.compactCache = tuple(self.pattern.compact(p, all=True) for p in self.parsings)
			return self.compactCache
		else:
			# Not bothering to cache in the non-all case because it's typically only used once:
			return self.pattern.compact(self.parsings[0])

	def __str__(self):
		if not self.parsings:
			return "[ERROR: Empty Match]"
		s = self.pattern.getName()
		if self.attrs is not None:
			s += "("+str(self.attrs)+")"
		if len(self.parsings) > 1:
			s += "{"
		s += esrapyMisc.parsingStr(self.parsings[0])
		for p in self.parsings[1:]:
			s += ", "+esrapyMisc.parsingStr(p)
		if len(self.parsings) > 1:
			s += "}"
		return s


class Parser:
	def __init__(self, source, stopAtFirst=True):
		"""Creates a parser bound to a particular source text.

		The simplest way to use this parser is with:

		    match = pattern.match(sourceText)

		More directly, the following is nearly equivalent except that this
		does not support the matchToEnd flag (which is enabled by default
		in the above):

		    match = Parser(source).parse(pattern)

		If stopAtFirst is true (the default), then the first matching
		interpretation will be returned -- i.e., the parser will behave
		deterministically.  Otherwise, all possible interpretations will
		be returned, and you can pick through them as you please.

		If you are implementing a Pattern subclass, you will want to call

		    parser.notify(pattern, start, end, parsing)

		for each valid match you find.  And if you need to search for
		sub-patterns in order to complete your own, do not call them
		directly, rather use:

		    parser.spawn(pattern, pos, notifyFunc, notifyData)

		which tells the parser to call notifyFunc(notifyData, parser, match)
		each time pattern is found at pos.

		The one peculiarity is that if you are going to spawn multiple
		searches for alternative patterns, you should do so in reverse
		order, as the last will be tried first.  Similarly, if you are
		interleaving notify calls with those (such as where a nil pattern
		falls somewhere in a prioretized list of non-nil patterns), that
		too should fall in reverse order.
		"""

		# The text to be parsed:
		self.source = source

		self.stopAtFirst = stopAtFirst

		# matches is a list of all known matches, indexed by pattern and exact span
		# done is a list of all known matches, indexed by pattern and start pos only
		# waiting is a list of all objects waiting on a particular pattern and start pos.
		#
		self.matches = {}	# (id(pattern), start, end) -> match
		self.done    = {}	# (id(pattern), start) -> [match, match, ...]
		self.waiting = {}	# (id(pattern), start) -> [(notifyFunc, notifyData), ...]
		self.pats    = {}	# id(pattern) -> pattern  ; Only used for error reporting!

	def reset(self):
		"Call this before re-using a Parser (with a new pattern on the same source)"
		self.maxpos  = 0					# How far did we get into the source?  (Useful for syntax errors..)
		self.results = []
		self.stack   = esrapyMisc.SimpleQueue()	# Our own "recursion" stack.

	def parse(self, pattern, startPos=0, debug=0):
		"Parses <source>[<startPos>:] with <pattern>, returning one Match (or a list of them if not StopAtFirst)"

		self.reset()
		self.debug = debug
		self.spawn(pattern, startPos, self.selfNotify)
		self.run()
		if not self.results:
			# What's the right way to report this?
			if self.debug: self.showExpecting()
			raise SyntaxError, "Syntax error on line %d at character %d"%(
						1 + self.source.count("\n", 0, self.maxpos),
						self.maxpos - self.source.rfind("\n", 0, self.maxpos))
		if self.stopAtFirst:
			return self.results[0]
		else:
			return self.results

	def selfNotify(self, data, parser, match):
		self.results.append(match)

	#
	# This trio of routines is just to eliminate recursion so we don't blow the python stack.
	# (Although it does have the side effect that the objects that call more than one of
	#  these in a row need to do so in reverse order...)
	#
	def spawn(self, *a, **b):
		"See spawn_()"
		self.stack.push((self.spawn_, a, b))

	def notify(self, *a, **b):
		"See notify_()"
		self.stack.push((self.notify_, a, b))

	def run(self):
		for func, a, b in self.stack:
			if self.stopAtFirst and self.results:
				return	# Terminate early if we only want the first valid match.
			func(*a, **b)

	def spawn_(self, pattern, pos, notifyFunc, notifyData=None):
		"Call <notifyFunc>(<notifyData>, parser, match) each time <pattern> is found at <pos>."

		self.pats[id(pattern)] = pattern	# Only used for error reporting.

		# See if this pattern has already been spawned here...
		key     = (id(pattern), pos)
		waiting = self.waiting.get(key)

		if waiting:
			# If it's already spawned, add the new waiter to the existing list
			#  so they'll be notified of future matches, and then notify them
			#  of any past matches:
			if self.debug>1: print "%5d+ %s (%s)"%(
					pos,pattern,str(notifyFunc.im_class)+"."+notifyFunc.im_func.__name__)
			waiting.append((notifyFunc, notifyData))
			done = self.done.get(key)
			if done:
				for match in tuple(done):	# tuple is to snapshot in case it changes
					notifyFunc(notifyData, self, match)
		else:
			# If it hasn't been spawned, spawn it!
			if self.debug>1: print "%5d: %s (%s)"%(
					pos,pattern,str(notifyFunc.im_class)+"."+notifyFunc.im_func.__name__)
			self.waiting[key] = [(notifyFunc, notifyData)]
			pattern.spawn(self, pos)

	def notify_(self, pattern, start, end, parsing='', attrs=None):
		"""Notify us that <pattern> was found from <start> to <end> with <parsing>.

		Typically this is called by a spawned pattern to let us know it found a match.
		"""

		if self.debug>1:
			if end > start+20:
				print "%d->%d: Found %s (%s...%s)"%(start, end, pattern,
					repr(self.source[start:start+8]), repr(self.source[end-8:end]))
			else:
				print "%d->%d: Found %s (%s)"%(start, end, pattern, repr(self.source[start:end]))

		if end > self.maxpos:
			self.maxpos = end

		# Check for existing match object, or create one:
		if attrs is None:
			key = (id(pattern), start, end)
		else:
			key = (id(pattern), start, end, attrs)
		match = self.matches.get(key)
		if match:
			match.addParsing(parsing)
			return	# No need to notify the world if we've already done so.
		match = Match(pattern, start, end, parsing, attrs)
		self.matches[key] = match

		# Add this match to the list at this start position:
		key  = (id(pattern), start)
		done = self.done.get(key)
		if not done:
			done = []
			self.done[key] = done
		done.append(match)

		# Notify anybody who's already waiting:
		waiting = self.waiting.get(key)
		if waiting:
			for func, data in tuple(waiting):	# tuple is to snapshot the list in case it changes.
				func(data, self, match)

	def showExpecting(self):
		"Prints info about what we're expecting at self.maxpos -- i.e., where we failed."
		print "Found on line %d at charater %d (pos %d): %s"%(
						1 + self.source.count("\n", 0, self.maxpos),
						self.maxpos - self.source.rfind("\n", 0, self.maxpos),
						self.maxpos,
						repr(self.source[self.maxpos:self.maxpos+40]))
		for patId, pos in self.waiting.keys():
			if pos == self.maxpos:
				print "Expecting:", self.pats[patId]

