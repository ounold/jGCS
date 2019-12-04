import MADFA
import sys
import random
import esrapy
import time
import os
import os.path

def catenation(U, Y):
  X = set()
  for u in U:
    for y in Y:
      X.add(u+y)
  return X

random.seed()

def evalW1(x, W1, A):
  s = 0
  for i in xrange(len(x)):
    s = A["delta"][(s, x[i])]
    pref = x[:i+1]
    W1[s].add(pref)

def R1R2(subset, W1, W2):
  U = set()
  V = W2[list(subset)[0]].copy()
  for s in subset:
    U |= W1[s]
    V &= W2[s]
  return (U, V)

def mozna_poprawic(P, T, states, W1, W2):
  n = len(catenation(T[0], T[1]))
  wynik = []
  for i in states:
    if i not in P:
      U = T[0] | W1[i]
      V = T[1] & W2[i]
      c = len(catenation(U, V))
      if (c > n) and (U != set([''])) and (V != set([''])):
        wynik.append(i)
  return wynik

def split(X, alfabet):
  A = MADFA.minDFA(X, alfabet)
  W1 = {}
  W2 = {}
  # W3 = {}
  for (k, s) in A["Q"].iteritems():
    W2[s] = k
    W1[s] = set()
    # W1[s] = from_initial_to(s, A)
    # W3[s] = catenation(W1[s], W2[s])
  W1[0].add("")
  for x in X:
    if x != "":
      evalW1(x, W1, A)
  states = A["Q"].values()
  licznik = 0
  k = 0
  moc_states = len(states)
  # time_limit = moc_states
  time_limit = max(min(len(X), moc_states), 10)
  dnslowa = -1
  for x in X:
    if len(x) > dnslowa:
      dnslowa = len(x)
      nslowo = x
  najlepszy_wynik = 1
  najlepsze_zbiory = (frozenset([nslowo[:1]]), frozenset([nslowo[1:]]), frozenset(X - set([nslowo])))
  while (licznik < time_limit):
    licznik += 1
    i = random.choice(states)
    P = set([i])
    (U, V) = R1R2(P, W1, W2)
    J = mozna_poprawic(P, (U, V), states, W1, W2)
    while len(J) > 0:
      j = random.choice(J)
      P.add(j)
      U |= W1[j]
      V &= W2[j]
      J = mozna_poprawic(P, (U, V), states, W1, W2)
    Y = catenation(U, V)
    if (len(Y) >= najlepszy_wynik) and (U != set([''])) and (V != set([''])):
      najlepszy_wynik = len(Y)
      najlepsze_zbiory = (frozenset(U), frozenset(V), frozenset(X-Y))
  return najlepsze_zbiory

def contains(X, V, K):
  if X in V:
    return V[X]
  if len(X) >= K:
    for A in V.iterkeys():
      if (len(A) >= K) and (X <= A):
        return V[A]
  return None

def infer(X, alfabet, P, V, nr):
  ix = nr[0]
  V[X] = ix
  # print nr[0]
  nr[0] += 1
  if len(X) == 1:
    s = list(X)[0]
    P.append((ix, s))
  elif X <= (alfabet | set([""])):
    for s in X:
      P.append((ix, s))
  else:
    (A, B, C) = split(X, alfabet)
    if A in V:
      va = V[A]
    else:
      va = infer(A, alfabet, P, V, nr)
    if B in V:
      vb = V[B]
    else:
      vb = infer(B, alfabet, P, V, nr)
    P.append((ix, (va, vb)))
    if len(C) > 0:
      if C in V:
        vc = V[C]
      else:
        vc = infer(C, alfabet, P, V, nr)
      P.append((ix, vc))
  return ix

def postprocessing(P, V, n):
  Q = []
  for (A, alfa) in P:
    if isinstance(alfa, str):
      if len(alfa) > 1:
        m = 0
        for Z in V.iterkeys():
          if alfa in Z and len(Z) > m:
            m = len(Z)
            z = Z
        if m > 0:
          Q.append((A, V[z]))
  P += Q

def prod2esrapy(P):
  zm = set()
  for (A, alfa) in P:
    zm.add(A)
  wiersze = {}
  for i in sorted(zm):
    wiersze[i] = "V"+str(i)+" = "
  for (A, alfa) in P:
    if isinstance(alfa, tuple):
      wiersze[A] += "V"+str(alfa[0])+" "+"V"+str(alfa[1])+" | "
    elif isinstance(alfa, int) and A != alfa:
      wiersze[A] += "V"+str(alfa)+" | "
    elif isinstance(alfa, str):
      wiersze[A] += "'"+alfa+"'"+" | "
  wynik = ""
  for i in reversed(sorted(zm)):
    wynik += wiersze[i][:-3]+"\n"
  return wynik

def readWords(file_name):
    """From Abbadingo competition format
    """
    positives = set()
    negatives = set()
    file = open(file_name, 'r')
    for line in file:
        tab = line.split()
        cls, length, symbols = tab[0], int(tab[1]), tab[2:]
        s = "".join(symbols)
        if cls == '1':
            positives.add(s)
        else:
            negatives.add(s)
    file.close()
    assert len(positives & negatives) == 0
    return positives, negatives

def wczytajPlik():
  nazwa = raw_input("Podaj nazwe pliku: ")
  inList = open(nazwa, 'rU').readlines()
  nd = map(int, inList[0].split())
  alfabet = set([ chr(i) for i in xrange(ord('a'), ord('a') + nd[1] + 1) ])
  # alfabet = set(['0', '1'])
  X = set()
  Y = set()
  for wiersz in inList[1:]:
    w = wiersz.split()
    if w[0] == '1':
      slowo = ""
      for znak in w[2:]:
        slowo += znak
      if len(slowo) <= 20:
        X.add(slowo)
    else:
      slowo = ""
      for znak in w[2:]:
        slowo += znak
      if len(slowo) <= 20:
        Y.add(slowo)
  print "Wczytalem ", len(X)+len(Y), " slow."
  return (frozenset(X), frozenset(Y), alfabet)

def dobra_gramatyka(Produkcje, Y):
  gtekst = prod2esrapy(Produkcje)
  pat = esrapy.compile(gtekst)
  for s in Y:
    try:
      pat.match(s)
      return False
    except:
      pass
  return True

def akceptuje_wszystkie(Produkcje, X):
  gtekst = prod2esrapy(Produkcje)
  try:
    pat = esrapy.compile(gtekst)
  except:
    return False
  for s in X:
    try:
      pat.match(s)
    except:
      return False
  return True

def polepszony(Produkcje, Zmienne, Y):
  if len(Zmienne) == 1:
    return (None, None)
  for (zi, ni) in Zmienne.iteritems():
    for (zj, nj) in Zmienne.iteritems():
      if ni < nj and len(zj & zi) > 0:
        P = set()
        for (A, alfa) in Produkcje:
          if A == nj: A = ni
          if isinstance(alfa, tuple):
            (B, C) = alfa
            if B == nj: B = ni
            if C == nj: C = ni
            P.add((A, (B, C)))
          elif isinstance(alfa, int):
            if alfa == nj: B = ni
            else: B = alfa
            if A != B:
              P.add((A, B))
          else:
            P.add((A, alfa))
        if dobra_gramatyka(P, Y):
          nowy = zi | zj
          del Zmienne[zi]
          del Zmienne[zj]
          Zmienne[nowy] = ni
          return (P, Zmienne)
  return (None, None)

def ma_symbol_S(P):
  for (p, alfa) in P:
    if p == 0:
      return True
  return False

def wyluskaj_zmienne(P):
  zm = set()
  for (A, alfa) in P:
    zm.add(A)
  return zm

def usun_zbedne(P, X):
  zmienne = wyluskaj_zmienne(P) - set([0])
  for zm in zmienne:
    Q = P.copy()
    for (A, alfa) in Q:
      if (A, alfa) in P:
        if A == zm:
          P.remove((A, alfa))
        else:
          if isinstance(alfa, tuple) and ((alfa[0] == zm) or (alfa[1] == zm)):
            P.remove((A, alfa))
          elif isinstance(alfa, int) and alfa == zm:
            P.remove((A, alfa))
    if not akceptuje_wszystkie(P, X) or not ma_symbol_S(P):
      P = Q.copy()
    else:
      print "Usunieto zmienna", zm
  dalej = True
  while dalej:
    Q = P.copy()
    dalej = False
    for p in Q:
      P.remove(p)
      if not akceptuje_wszystkie(P, X) or not ma_symbol_S(P):
        P.add(p)
      else:
        dalej = True
  return P

def slowa(alfabet, n):
  tab = [""]
  for i in xrange(n+1):
    tab2 = []
    for s in tab:
      for znak in alfabet:
        tab2.append(s+znak)
    tab += tab2
  X = set(tab)
  return frozenset(X)

def rozmiar(P):
  wynik = 0
  for (A, alfa) in P:
    if isinstance(alfa, str):
      wynik += len(alfa)
    elif isinstance(alfa, tuple):
      wynik += 2
    else:
      wynik += 1
  return wynik

def synthesize(X, Y, alfabet): # frozenset, frozenset, set
    czas_start = time.clock()
    print czas_start
    minimalna = 99999
    minProd = set()
    for licznik in xrange(10):
      print licznik
      Produkcje = []
      Zmienne = {}
      nr = [0]
      infer(X, alfabet, Produkcje, Zmienne, nr)
      Produkcje = set(Produkcje)
      if not akceptuje_wszystkie(Produkcje, X):
        print "infer -> brak akceptacji"
        sys.exit()
      (Produkcje, Zmienne) = polepszony(Produkcje, Zmienne, Y)
      while Produkcje:
        r = rozmiar(Produkcje)
        if r < minimalna:
          minimalna = r
          minProd = Produkcje
        (Produkcje, Zmienne) = polepszony(Produkcje, Zmienne, Y)
    print "\n"
    print "Liczba produkcji", len(minProd)
    minProd = usun_zbedne(minProd, X)
    czas_stop = time.clock()
    print czas_stop
    print czas_stop - czas_start
    print "\n"
    print "Liczba produkcji", len(minProd)
    return minProd

res_file = open("results.txt", "w")
print >>res_file, "Test   Precision Sensitivity Specificity   F-measure"
for iteration in xrange(10):
    for sample_num in xrange(1, 31):
      print "Train and test", sample_num
      train_name = os.path.join(".", "train", "train"+str(sample_num)+".txt")
      test_name = os.path.join(".", "test", "test"+str(sample_num)+".txt")
      X_train, Y_train = readWords(train_name)
      X_test, Y_test = readWords(test_name)
      alphabet = set(c for s in X_train | Y_train for c in s)
      minProd = synthesize(frozenset(X_train), frozenset(Y_train), alphabet)
      pat = esrapy.compile(prod2esrapy(minProd))
      print "Weryfikacja..."
      for slowo in X_train:
        try:
          pat.match(slowo)
        except:
          print "Nie zaakceptowal przykladu", slowo
          sys.exit()
      for slowo in Y_train:
        try:
          pat.match(slowo)
          print "Zaakceptowal kontrprzyklad", slowo
          sys.exit()
        except:
          pass
      print "przebiegla pozytywnie"
      tp = fp = fn = tn = 0.0
      for slowo in X_test:
        try:
          pat.match(slowo)
          tp += 1
        except:
          fn += 1
      for slowo in Y_test:
        try:
          pat.match(slowo)
          fp += 1
        except:
          tn += 1
      p = tp + fn
      n = fp + tn
      p_prim = tp + fp
      precision = tp/p_prim
      sensitivity = tp/p
      specificity = tn/n
      F1 = 2.0*precision*sensitivity/(precision + sensitivity)
      print >>res_file, "{:4}{:12.4f}{:12.4f}{:12.4f}{:12.4f}".format(sample_num, precision, sensitivity, specificity, F1)
res_file.close()
