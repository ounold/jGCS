def lq(w, X):
  U = set()
  for x in X:
    if x[:1] == w:
      U.add(x[1:])
  if w in X:
    U.add("")
  return frozenset(U)

def left_quotients(X, T):
  for a in T:
    U = lq(a, X)
    if len(U) > 0:
      yield (a, frozenset(U))

def minimalADFA(X, A):
  A["Q"][X] = A["q"]  
  if '' in X:
    A["F"].add(A["q"])
  p = A["q"]
  A["q"] += 1
  for (a, U) in left_quotients(X, A["T"]):
    if U in A["Q"]:
      A["delta"][(p, a)] = A["Q"][U]
    else:
      A["delta"][(p, a)] = minimalADFA(U, A)
  return p

def transitionsFrom(s, A):
  res = []
  for a in A["T"]:
    if (s, a) in A["delta"]:
      res.append((s, A["delta"][(s, a)], a))
  return res
  
def minDFA(X, T):
  A = {}
  A["delta"] = {}
  A["F"] = set()
  A["T"] = T
  A["Q"] = {}
  A["q"] = 0
  minimalADFA(X, A)
  return A

