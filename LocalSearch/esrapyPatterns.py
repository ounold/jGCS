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
r"""A module for parsing text with context-free grammars.

To parse a body of text, first build an object describing the grammar (a Pattern),
then apply it to the text.  For example:

    pattern = NtoM(Sequence((Regex("[^=]*"), Regex("="), Regex(r"([^\n])\n", 1))), 5, 10)

    parseTree = pattern.match(sourceText)

will return a valid parse tree if sourceText contains anywhere
from 5 to 10 lines of the form "some text=more text\n".

See the docs for the individual Pattern types below for details.
"""

from esrapyCore import Pattern, Match, Parser
import esrapyMisc
import string
import re

class Regex(Pattern):
	'Any python regular expression ("re" module).'
	name = "RegEx"

	def init(self, regex, group=0, skip=None):
		"""Group is which group to return as the string value of this expr.

		If group<0, then the regex is treated as a simple literal string.

		Skip is an optional regular expression (always treated as a regex,
		never a literal string) which will be consumed and discarded before
		regex is matched (regex may still be either a regular expression or
		a literal string).  Typically, skip would be a white space pattern,
		such as "[ \t\n\r]*" (or "[ \t\n\r]+" if the empty string is not
		acceptable).
		"""
		self.regex = regex
		self.skip  = skip
		if group < 0:
			self.rx = None
		else:
			self.rx = re.compile(regex, re.DOTALL|re.MULTILINE)
		if skip:
			self.sx = re.compile(skip, re.DOTALL|re.MULTILINE)
		self.group = group

	def spawn(self, parser, pos):
		# This block just sucks up the skip pattern (usually white space):
		if self.skip:
			m = self.sx.match(parser.source, pos)
			if not m: return
			pos2 = m.end()
		else:
			pos2 = pos
		# Now back to our regular programming...
		if self.rx:
			m = self.rx.match(parser.source, pos2)
			if m:
				parser.notify(self, pos, m.end(), m.group(self.group))
		else: # Just a literal string match
			if parser.source.startswith(self.regex, pos2):
				parser.notify(self, pos, pos2+len(self.regex), self.regex)

	def compact(self, parsing, **flags):
		return parsing

	def str(self):
		if self.skip:
			s = "<%s>"%self.skip
		else:
			s = ''
		if self.rx:
			if self.group:
				return s+  "<%s>@%s"%(self.regex, self.group)
			else:
				return s+ "<%s>"%self.regex
		else:
			return s+ "'" + self.regex + "'"


def String(s):
	'This returns a Pattern representing a literal string.'
	return Regex(s, -1)

class OneOfSet(Pattern):
	"""One of a set of possible patterns with optionally associated names and/or precedences"

	In the simplest form, just pass a list of Patterns (in the order
	you would like them to be tried), and the first that works (in
	the broader context--i.e., not necessarily the first match) will
	be used.

	Any of the Patterns in the list can be replaced with a tuple
	containing the pattern, and either a name, an integer priority,
	or both (in any order).

	If a name is provided, the compact value will be the tuple (name, value).

	If a precedence is provided, all items until the next provided precedence
	will be assigned that precedence.

	Alternately, set may be another OneOfSet Pattern, in which case
	that pattern's set will be used instead (with the local minp overriding).

	The optional minp parameter will prune the set to only those with a
	precedence >= minp.  For convenience, these are equivalent:

	    p = OneOfSet(set)
	    q = OneOfSet(set, n)    # or q = OneOfSet(p, n)
	
	and

	    p = OneOfSet(set)
	    q = p.subset(n)

	Except that the latter will return the same object every time (for
	the same n), which is better for the parser.  Also, note that:

	    p = OneOfSet()
		q = p.subset(n)
		p.init(set)

	does work as expected and is thus also equivalent to the above.
	"""
	name = "Set"

	subsets = {}	# Indexed by parent set and minp.

	def init(self, set, minp=0):
		self.minp     = minp
		self.set      = set
		if isinstance(set, OneOfSet):
			self.parent = set
			self.set    = None
		else:
			self.parent = None
			self.patterns = []	# Uniform list of (pattern, precedence), in reverse order
			self.names    = {}	# Dictionary mapping each specific pattern to an associated name.
			# Construct .patterns and .names from the parameters:
			prec = 0
			for p in set:
				if isinstance(p, Pattern):
					self.patterns.insert(0, (p, prec))
				else:
					pp   = None
					name = None
					for v in p:
						if isinstance(v, int):
							prec = v
						elif isinstance(v, basestring):
							if v[0] in string.digits:
								prec = int(v)
							else:
								name = v
						elif isinstance(v, Pattern):
							pp = v
						else:
							raise ValueError, "OneOfSet passed odd parameter (%s from %s)"%(v,p)
					if not pp: raise ValueError, "OneOfSet passed tupple with no Pattern (%s)."%p
					self.patterns.insert(0, (pp, prec))
					if name:
						self.names[id(pp)] = name

	def subset(self, n):
		s = OneOfSet.subsets.get((self, n))
		if not s:
			s = OneOfSet(self, n)
			OneOfSet.subsets[(self, n)] = s
		return s

	def postInit(self):
		if self.set or not self.parent: return
		self.parent.postInit()
		self.set      = self.parent.set
		self.patterns = self.parent.patterns
		self.names    = self.parent.names

	def spawn(self, parser, pos):
		# Suck our definition in from our parent if necessary, at the last minute:
		if not self.set and self.parent:
			self.postInit()
		for pattern, precedence in self.patterns:
			if precedence >= self.minp:
				parser.spawn(pattern, pos, self.notify)

	def notify(self, data, parser, match):
		parser.notify(self, match.start, match.end, (match,))

	def compact(self, parsing, **flags):
		match = parsing[0]
		comp  = match.compact(**flags)
		name  = self.names.get(id(match.pattern))
		if name:
			return (name, comp)
		else:
			return comp

	def str(self):
		self.postInit()
		if self.parent:
			return "%s@%d"%(self.parent, self.minp)
		return esrapyMisc.deepStr([(tup[1], tup[0]) for tup in reversed(self.patterns) if tup[1]>=self.minp], '{}')


class Sequence(Pattern):
	"An exact sequence of patterns."
	name = "Sequence"

	def init(self, seq):
		"""Each item in sequence seq is either:

		- a Pattern (or a tuple of the form (anthingFalse, pattern))
		- a Tuple of the form (name, pattern) for some string name,

		The difference between these only affects the compact (default)
		representation.

		If all items are of the first form, a simple list is returned.

		If multiple items are of the second form, a dictionary of only those
		items is returned.

		If only one item is of the second form, that item is returned directly.

		E.g., where var, equals, and val are all existing Patterns:

		   Sequence((var, equals, val)).match("foo=10")
		   ... will return ["foo", "=", "10"]

		   Sequence((("a", var), equals, ("b", val))).match("foo=10")
		   ... will return {"a":"foo", "b":"10"}

		   Sequence((var, equals, ('_', val))).match("foo=10")
		   ... will return "10"
		"""

		rawseq = []	# the sequence of patterns with no adornments.
		key    = [] # list of (index, name) for building dict.

		for n, p in enumerate(seq):
			if isinstance(p, Pattern):
				rawseq.append(p)
			else:
				rawseq.append(p[1])
				if p[0]: key.append((n, p[0]))
		if not key:
			key = None
		elif len(key) == 1:
			key = key[0][0]
		self.seq = rawseq
		self.len = len(rawseq)
		self.key = key

	def spawn(self, parser, pos):
		parser.spawn(self.seq[0], pos, self.notify, ())

	def notify(self, data, parser, match):

		parsing = data + (match,)
		done    = len(parsing)

		if done >= self.len: # We're complete!
			parser.notify(self, parsing[0].start, match.end, parsing)
		else: # Not done yet, so spawn the next item in the sequence:
			parser.spawn(self.seq[done], match.end, self.notify, parsing)

	def compact(self, parsing, **flags):
		if self.key is None:
			return [m.compact(**flags) for m in parsing]
		elif isinstance(self.key, int):
			return parsing[self.key].compact(**flags)
		else:
			return esrapyMisc.MetaDict((name, parsing[index].compact(**flags)) for index, name in self.key)

	def str(self):
		return esrapyMisc.deepStr(self.seq, '()')

class NtoM(Pattern):
	"""N to M (inclusive) instances of something end-to-end, with optional separator.

	In addition to the primary repeating pattern, a second pattern may be
	provided which will be used as a separator between instances of the primary
	pattern.  For instance, a common separator might be a white-space buffered
	comma, implying a comma-separated list.  Note the key difference from the
	obvious (pattern separator)* is that no trailing separator is expected after
	the last pattern.

	The return value of this pattern is always a simple tuple containing
	the primary patterns.  (The separators, if any, are discarded.)
	"""
	name = "*"

	def init(self, pattern, n=0, m=None, separator=None, greedy=True):
		"If greedy is True, the longest match is prefered, otherwise the opposite."

		self.pattern = pattern
		self.n       = n
		self.m       = m
		self.greedy  = greedy
		self.sep     = separator

	def spawn(self, parser, pos):
		if (not self.greedy) and self.m is not 0:
			parser.spawn(self.pattern, pos, self.notify, ())
		if 0 >= self.n:
			parser.notify(self, pos, pos, ())	# We accept the nil pattern if n<1
		if self.greedy and self.m is not 0:
			parser.spawn(self.pattern, pos, self.notify, ())

	def notify(self, data, parser, match):
		parsing = data + (match,)
		if match.end == match.start and not self.m:
			raise ValueError, "Infinite repetition of nil pattern in %s"%self
		if self.greedy and len(parsing) >= self.n:
			parser.notify(self, parsing[0].start, match.end, parsing)
		if self.m is None or len(parsing) < self.m:
			if self.sep:
				parser.spawn(self.sep, match.end, self.notify2, parsing)
			else:
				parser.spawn(self.pattern, match.end, self.notify, parsing)
		if not self.greedy and len(parsing) >= self.n:
			parser.notify(self, parsing[0].start, match.end, parsing)

	def notify2(self, parsing, parser, match):
		"Called when we've just matched a separator."
		parser.spawn(self.pattern, match.end, self.notify, parsing)

	def compact(self, parsing, **flags):
		return [m.compact(**flags) for m in parsing]

	def str(self):
		if self.sep:
			return esrapyMisc.deepStr(self.pattern) + "/" + \
					esrapyMisc.deepStr(self.sep) + '{%s,%s;%s}'%(self.n, self.m, self.greedy)
		else:
			return esrapyMisc.deepStr(self.pattern) + '{%s,%s;%s}'%(self.n, self.m, self.greedy)

def Optional(pat):
	"Returns a pattern representing a single optional occurance of pat."
	return NtoM(pat, 0, 1)

class Ghost(Pattern):
	"A pattern that matches without consuming input."

	name = 'Ghost'

	def init(self, pattern):
		self.pattern = pattern

	def spawn(self, parser, pos):
		parser.spawn(self.pattern, pos, self.notify)

	def notify(self, data, parser, match):
		parser.notify(self, match.start, match.start, (match,))

	def compact(self, parsing, **flags):
		return parsing[0].compact(**flags)

	def str(self):
		return "~"+esrapyMisc.deepStr(self.pattern)


class Key(Pattern):
	"This pattern matches the empty string and returns a provided keyword in the compact representation"
	name = "Key"

	def init(self, key):
		self.key = key

	def spawn(self, parser, pos):
		parser.notify(self, pos, pos)

	def compact(self, parsing, **flags):
		return self.key

	def str(self):
		return "<<%s>>"%self.key


class Named(Pattern):
	"This just assigns a name to a pattern during compact(), returned as a tuple (name, value)"
	name = "Named"

	def init(self, name, pattern):
		self.name    = name
		self.pattern = pattern

	def spawn(self, parser, pos):
		parser.spawn(self.pattern, pos, self.notify)

	def notify(self, data, parser, match):
		parser.notify(self, match.start, match.end, (match,))

	def compact(self, parsing, **flags):
		return (self.name, parsing[0].compact(**flags))

	def str(self):
		return "%s::%s"%(self.name,self.pattern)

