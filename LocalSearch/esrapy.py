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

"""Parses Patterns from a textual description.

This is the module that turns a textual description of a grammar
into an object that can actually be used for parsing.  Text in, Pattern
out (similar to re.compile() from the standard python regular expression
module).

It works internally by creating Pattern objects directly with python
calls, so this module also serves as an example of how to procedurally
construct Patterns.

I considered moving much of this into the associated objects, so for
instance the Sequence pattern would declare its own syntax and self-parsing
method, but for the moment I like having this entire module factored out
from the rest so it can be entirely omitted if you aren't compiling
patterns.

(Also, note one could fairly easily enable the Pattern classes to generate
python code describing themselves, which would allow one to compile a
pattern description file into a procedural definition.)

Typical usage:

pattern = esrapy.compile(file("myGrammar.pat").read())
parseTree = pattern.match(file("source.goo").read())
"""

from esrapyPatterns import *

#---------------- Here begins the syntax of Pattern description files --------------

# Comment-sucking optional white space with indentation:
pSpace = Regex(r"([\r\n]*([ \t]+|[ \t]*#[^\n\r]*))*")

# Comment-sucking white space to (and including) end of line (or end of source):
pEndLine  = Regex(r"([ \t]*(#[^\r\n]*)?([\n\r]|\Z))+")
# Optional version of same:
pEndLine0 = Regex(r"([ \t]*(#[^\r\n]*)?([\n\r]|\Z))*")

# Variable name, and integer:
pVarname = Regex(r"[a-zA-Z_]+[a-zA-Z_0-9]*")
pInt     = Regex(r"[0-9]+")


# This will be the central set enumerating all the different patterns.
# We'll init/parameterize it later since it is ultimately self-referential:
pBase = OneOfSet()

def pList(pat, n=0, m=None, sep=','):
	"returns a Pattern describing a list of n to m pats in a row separated by sep, with whitespace."
	return NtoM(pat, n, m, separator=Sequence((pSpace, String(sep), pSpace)))

pBaseSep = Sequence((("base", pBase.subset(2)),
					 ("sep", Optional(Sequence((pSpace, String("/"), pSpace,
						('_', pBase.subset(2))))))))

# The base set of patterns:
pBase.init((
	(0, "set", pList(
					Sequence((
						("attrs", Optional(Sequence((('_', pList(OneOfSet((pInt, pVarname)), 1, 2, ',')),
								pSpace, String(';'), pSpace)))),
						("pat", pBase.subset(1)))),
				2, sep='|')),
	(1, "seq", NtoM(Sequence((("name", Optional(Sequence((('_', pVarname), pSpace, String(':'), pSpace)))),
								("pat", pBase.subset(2)))), 2, separator=pSpace)),
	(2, "mod", Sequence((("base", pBaseSep), pSpace, ("op", Regex("[?*+]"))))),
	( "range", Sequence((
					("base", pBaseSep), pSpace,
					String("{"), pSpace,
					("range", pList(pInt, 1, 2, ',')),
					pSpace, String("}")))),
	(   "var", pVarname),
	(   "key", Sequence((String('\\'), pSpace, ('_', pVarname)))),
	("subset", Sequence((("var", pVarname), pSpace, String("@"), pSpace, ("limit", pInt)))),
	( "regex", Sequence((
				("expr", OneOfSet((Regex(r"<([^>]*)>", 1), Regex(r"`([^`]*)`", 1)))),
				("group", Optional(Sequence((pSpace, String("@"), pSpace, ('_', pInt)))))))),
	("string", OneOfSet((Regex(r'"([^"]*)"', 1), Regex(r"'([^']*)'", 1)))),
	Sequence((String("("), pSpace, ('_', pBase), pSpace, String(")")))
	))

# And the top level operation, assigning a pattern to a variable:
pAssign = Sequence((("var", pVarname), pSpace, String("="), pSpace, ("val", pBase), pEndLine))

# The assign includes trailing white space, but we still need to allow for heading:
pFile = Sequence((pEndLine0, ('_', NtoM(pAssign))))

#---------------- Thus ends the syntax of Pattern description files --------------

from pprint import pprint
def compile(patDef, debug=0, trailingSPACE=True):
	"""Compiles a textual pattern definition into a Pattern object.

	if trailingSPACE is true (the default), any default white space in
	the pattern, as assigned to the SPACE variable, will automatically
	tacked onto the butt of the returned pattern.

	If debug is 1, extra information will be displayed in the event of a syntax error
	in the pattern definition.

	If debug is -1, the parsed patterns will be (crudely) printed."""

	# Parse the definition into a parse tree (pt):
	pt = pFile.match(patDef, debug=debug)

	# Create a dict that indexes the definitions by name:
	defns = {}
	for assign in pt:
		defns[assign.var] = [assign.val] # Array so we can append the Pattern

	# Recursively build and then return the last-defined pattern:
	if debug<0:
		compile_var(defns, pt[-1].var)
		for name, val in defns.items():
			print '  ', name, "=", val[1].str()
	pat  = compile_var(defns, pt[-1].var)
	if trailingSPACE:
		skip = compile_getSkip(pat, defns)	# Spurious but acceptable use of pat here.
		if skip:
			pat = Sequence((('_', pat), Regex(skip)))	# Slam trailing SPACE on it...
	return pat

def register_var(defns, varname, pattern):
	if varname:
		assert len(defns[varname])==1
		defns[varname].append(pattern)
		pattern.setName(varname)	# Just for informational/printing purposes.

def compile_var(defns, varname):
	row = defns.get(varname)
	if not row:
		raise ValueError, "Pattern %s referenced but not defined."%varname
	if len(row)>1:
		return row[1] # Cached from earlier call
	return compile_base(row[0], defns, varname)

def compile_base(defn, defns, varname=None):
	"Same as compile_set and _seq, but for everything else..."
	type, defn = defn	# The incoming defn was a 2-tuple including a type.
	if type == 'var':	# Var is a special case...
		return compile_var(defns, defn)
	#print "Compile %s as %s:"%(varname, type)
	#pprint(defn)
	constructor, compiler = baseTypes[type]
	pattern = constructor()					# Postpone initialization
	register_var(defns, varname, pattern)	# Until we've registered it by name
	compiler(pattern, defn, defns)			# Then initialize it from the defn
	return pattern

# Here are the compilers for the base types:

def compile_getSkip(pat, defns):
	if pat.name == "SPACE": return None
	if not defns.has_key("SPACE"): return None
	skip = compile_var(defns, "SPACE")
	if not isinstance(skip, Regex):
		raise ValueError, "The special var SPACE is required to be a regular expression."
	return skip.regex	# In theory this should be resolved by now...

def compile_string(pat, defn, defns):
	pat.init(defn, -1, skip=compile_getSkip(pat, defns))

def compile_regex(pat, defn, defns):
	if defn.group:
		pat.init(defn.expr, int(defn.group[0]), skip=compile_getSkip(pat, defns))
	else:
		pat.init(defn.expr, skip=compile_getSkip(pat, defns))

def compile_set_item(item, defns):
	if item.attrs:
		return tuple(item.attrs[0]) + (compile_base(item.pat, defns),)
	else:
		return compile_base(item.pat, defns)

def compile_set(pat, defn, defns):
	pat.init([compile_set_item(item, defns) for item in defn])

def compile_subset(pat, defn, defns):
	pat.init(compile_var(defns, defn.var), int(defn.limit))

def compile_seq_item(item, defns):
	if item.name:
		return (item.name[0], compile_base(item.pat, defns))
	else:
		return compile_base(item.pat, defns)

def compile_seq(pat, defn, defns):
	pat.init([compile_seq_item(item, defns) for item in defn])

def compile_mod(pat, defn, defns):
	min, max = {'?':(0,1), '*':(0, None), '+':(1, None)}[defn.op]
	if defn.base.sep:
		pat.init(compile_base(defn.base.base, defns), min, max, separator=compile_base(defn.base.sep[0], defns))
	else:
		pat.init(compile_base(defn.base.base, defns), min, max)

def compile_range(pat, defn, defns):
	if len(defn.range) == 2:
		min, max = defn.range
		max = int(max)
	else:
		min = defn.range[0]
		max = None
	min = int(min)
	if defn.base.sep:
		pat.init(compile_base(defn.base.base, defns), min, max, separator=compile_base(defn.base.sep[0], defns))
	else:
		pat.init(compile_base(defn.base.base, defns), min, max)

def compile_key(pat, defn, defns):
	pat.init(defn)

baseTypes = {
	"string": (Regex   , compile_string),
	 "regex": (Regex   , compile_regex),
	   "set": (OneOfSet, compile_set),
	"subset": (OneOfSet, compile_subset),
	   "seq": (Sequence, compile_seq),
	   "mod": (NtoM    , compile_mod),
	 "range": (NtoM    , compile_range),
	   "key": (Key     , compile_key)
	}

# This just helps with debugging (makes for better extended-form syntax error messages):
pats = (
	(pSpace, 'pSpace'),
	(pEndLine, 'pEndLine'),
	(pEndLine0, 'pEndLine0'),
	(pVarname, 'pVarname'),
	(pInt, 'pInt'),
	(pBase, 'pBase'),
	(pBaseSep, 'pBaseSep'),
	(pAssign, 'pAssign'),
	(pFile, 'pFile'))
for pat, name in pats:
	pat.setName(name)

if __name__ == "__main__":

	print "Procedurally-defined patterns:"
	for pat, name in pats:
		print '  ', name, '=', pat.str()

	print "Textually-defined patterns:"
	print "Loading the file..."
	patFile = file("examples/esrapy.pat").read()

	print "Compiling the file..."
	p = compile(patFile, debug=-1)

	# This is purely for testing purposes; already been done once by compile()
	print "Parsing the file with the procedurally defined pattern..."
	pt = pFile.match(patFile, debug=1)

	print "Parsing the file with itself..."
	pt2 = p.match(patFile, debug=1)

	# Uncomment this to see what the parse tree looks like:
	#pprint(pt2)

	if pt==pt2:
		print "Yay, the textually and procedurally built patterns work the same."
	else:
		print "Boo, the textually and procedurally built patterns do not work the same."

