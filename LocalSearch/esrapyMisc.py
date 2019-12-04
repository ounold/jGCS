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
"Misc handy support utils."

class MetaDict(dict):
	"""A dictionary who's entries are accessible as fields.

	E.g.,  foo['x'] = 10  <->  foo.x = 10
	"""
	def __init__(self, *a, **b):
		dict.__init__(self, *a, **b)
		self.__dict__ = self


class Recursor:
	"""Recusion detection tracker.

	Typical use:

		if recursor.enter(ob):
			<more potentially recursive stuff...>
			recursor.leave(ob)
		else:
			<stuff to do when recursion is prevented>
	"""

	def __init__(self):
		self.all = {}

	def enter(self,ob):
		"Returns False if ob is already entered, otherwise enters it."
		if id(ob) in self.all:
			return False
		self.all[id(ob)] = ob
		return True

	def leave(self, ob):
		"Removes ob from the recursion list."
		del self.all[id(ob)]

rec = Recursor()

def deepStrList(l):
	if not l:
		return ''
	s = deepStr(l[0])
	for ob in l[1:]:
		s += ", "+deepStr(ob)
	return s

def deepStr(ob, brackets=None):
	"Like str() but dives all the way down (with automatic recursion clipping)..."
	if rec.enter(ob):
		if brackets:
			s = brackets[0] + deepStrList(ob) + brackets[1]
		elif isinstance(ob,list):
			s = "[" + deepStrList(ob) + "]"
		elif isinstance(ob,tuple):
			s = "(" + deepStrList(ob) + ")"
		elif hasattr(ob, 'deepStr'):
			s = ob.deepStr()
		else:
			s = str(ob)
		rec.leave(ob)
		return s
	else:
		return "..."

def parsingStr(p):
	"Returns a human-friendly string representation of a Parsing (i.e., any Match.parsings[n])"
	if isinstance(p,basestring):
		return "<" + p + ">"
	s = "["
	if len(p):
		s += str(p[0])
		for i in p[1:]:
			s += " "+str(i)
	return s+"]"

class SimpleQueue:
	"""A simple iterable queue.

		In addition to the usual direct manipulations outlined below,
		this queue is iterable with an implied .pop() so you can say, e.g.:

			s = SimpleQueue(...)
			for i in s:	# equivalent to the C "while (i = s.pop())"
				...
				s.push(...)

		And the iteration will continue until and if the Queue falls empty.

		Note that the queue is stored in reverse with the first item at
		the end because I suspect push/pop may work faster that way (no
		need to shift the array around)?
	"""

	def __init__(self):
		"Creates and empty queue."
		self.items = []

	def push(self, v):
		"Push an item on to the top of the queue.  Will be next out."
		self.items.append(v)

	def append(self, v):
		"Append an item to the end of the queue.  Will be last out."
		self.items.insert(0, v)

	def pop(self):
		"Pop an item from the top of the queue."
		return self.items.pop()

	def top(self):
		"Returns the top (next) item in the queue without popping it."
		if self.items:
			return self.items[-1]
		return None

	def bottom(self):
		"Returns the bottom (last) item in the queue without popping it."
		if self.items:
			return self.items[0]
		return None

	def empty(self):
		"Clears the queue."
		self.items = []

	def __iter__(self):
		return self

	def next(self):
		if self.items:
			return self.items.pop()
		raise StopIteration

