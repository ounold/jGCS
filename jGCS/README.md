### JGCS

#### Opis działania w pseudokodzie
```
funkcja wczytajZbiórTestowy():
    jeżeli podano zbiór testowy:
        wczytaj zbiór testowy
    w innym wypadku:
        potraktuj zbiór uczący jako testowy

funkcja inicjalizujGramatykę():
    jeżeli podano gramatykę:
        wczytaj gramatykę
    w innym wypadku:
        generuj losową gramatykę
        
funkcja budujSąsiedztwa():
    jeżeli tryb != IO:
        n := []
        dla każdego zdania pozytywnego:
            znajdź sąsiadów zgodnie z trybem
            ogranicz liczbę sąsiadów do maks. rozmiar sąsiedztwa
            dodaj sąsiadów do n
        dla każdego zdania negatywnego:
            przypisz do zdania liczbę wystąpień w n

funkcja przeprowadźEwaluację(i):
    przeprowadź ewaluację na zbiorze testowym
    zapisz wyniki ewaluacji dla iteracji i

funkcja wykonajIteracjęAlgorytmu():
    resetuj v i szacowaną liczbę użyć reguł
    dla każdego zdania w zbiorze:
        resetuj v reguł
        przeprowadź cyk
        aktualizuj v reguł
        aktualizuj szacowaną liczbę użyć reguł
    aktualizuj prawdopodobieństwa reguł
    
funkcja wykonajAlgorytm():
    i := 0
    dopóki prawda:
        jeżeli i = liczba iteracji
            przeprowadźEwaluację(i)
            przerwij pętlę
        jeśli krok ewaluacji != 0 oraz i % krok ewaluacji == 0:
            przeprowadźEwaluację(i)
        jeżeli podano warunki stopu i są spełnione
            przerwij pętlę
        wykonajIteracjęAlgorytmu()
        i++

wyczyść plik docelowy ewaluacji
wczytaj zbiór uczący
wczytajZbiórTestowy()
wczytaj warunki stopu algorytmu
r := 1
dopóki r <= liczba powtórzeń:
    inizjalizujGramatykę()
    normalizuj prawdopodobieństwa reguł
    budujSąsiedztwa()
    wykonajAlgorytm()
    dla każdej macierzy pomyłek w ewaluacji:
        dodaj rekord do pliku docelowego ewaluacji
    aktualizuj najlepszą gramatykę
    wyczyść ewaluację
    r++
zapisz najlepszą gramatykę do pliku
zapisz czasy wykonania do pliku
```

#### Wymagania
- Java w wersji 1.8

#### Aplikacja
Aplikację należy uruchamiać następującym poleceniem
```
java -jar <JAR> [-g <GRAMMAR>] -d <DATASET> [-v <TEST_DATASET>] -c <CONFIG> -e <EVALUATION_OUTPUT> 
[-o <GRAMMAR_OUTPUT>] [-r <REPEATS>] [-s] [-t <EXEC_TIMES>]
```
- `JAR` - plik wykonywalny aplikacji
- `-g GRAMMAR` - (opcjonalny) plik zawierający inicjalną gramatykę; w przypadku braku gramatyka jest generowana na podstawie zbioru uczącego
- `-d DATASET` - plik zawierający bazę sekwencji uczących
- `-v TEST_DATASET` - (opcjonalny) plik zawierający bazę sekwencji testowych; w przypadku braku wykorzystywany jest zbiór uczący
- `-c CONFIG` - plik konfiguracyjny aplikacji
- `-e EVALUATION_OUTPUT` - nazwa pliku CSV, do którego zostaną zapisane wyniki ewaluacji
- `-o GRAMMAR_OUTPUT` - (opcjonalny) nazwa pliku, do którego zostanie zapisana wynikowa gramatyka o najwyższej wartości F1
- `-r REPEATS` - (opcjonalny) liczba wykonań
- `-s` - (opcjonalny) tryb wsadowy
- `-t EXEC_TIMES` - (opcjonalny) nazwa pliku CSV, do którego zostaną zapisane średnie czasy wykonania poszczególnych części programu

Aplikacja może pracować w dwóch trybach - seryjnym i wsadowym. W trybie wsadowym należy odpowiadające sobie gramatyki i bazy przykładów umieścić w plikach o takich samych nazwach w różnych katalogach, a następnie podać jako parametry `GRAMMAR`, `DATASET`, `TEST_DATASET` i `GRAMMAR_OUTPUT` ścieżki katalogów, zamiast ścieżek konkretnych plików.

Plik wykonywalny i przykładowe dane oraz skrypty uruchomieniowe umieszczono w katalogu *bin*.

#### Gramatyka
Aplikacja obsługuje dwa formaty gramatyki: `SIMPLE` i `EXTENDED`.  Format musi zostać wybrany w pliku konfiguracyjnym.

##### Format SIMPLE
Inicjalną gramatykę należy podawać w pliku tekstowym o następującym formacie:
```regexp
gramatyka                       G -> (R;)*R
reguła                          R -> (N->NN|N->T)P?
s. nieterminalny                N -> S|E
s. startowy                     S -> \$
s. nieterm. niestart.           E -> [A-Z]
s. terminalny                   T -> [a-z]
prawdopodobieństwo              P -> \(\d(\.\d+)?([eE][-+]?\d+)?\)
```

W tym samym formacie zapisywana jest gramatyka wynikowa.

###### Uwagi
- Białe znaki pomiędzy tokenami są ignorowane
- Gramatyka musi zawierać symbol startowy
- Jeżeli nie podano prawdopodobieństwa reguły, zostanie ono przypisane automatycznie (patrz *Konfiguracja*)

###### Przykład
Gramatyka zdefiniowana następująco
```
A->a(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;$->AF 
```
zawiera:
- symbole terminalne `a, b`
- symbole nieterminalne `A, B, C, D, E, F`
- symbol startowy `$`
- reguły terminalne `A->a, B->b`
- reguły nieterminalne `C->AB, D->CA, E->BC, F->BD, $->AF`

##### Format EXTENDED
Inicjalną gramatykę należy podawać w pliku tekstowym o następującym formacie:
```regexp
gramatyka                       G -> (RL)*R
reguła                          R -> (N -> N N|N -> T) P?
s. nieterminalny                N -> S|E
s. startowy                     S -> S
s. nieterm. niestart.           E -> [^S\s]+
s. terminalny                   T -> '[^']+'|"[^"]+"
prawdopodobieństwo              P -> \(\d(\.\d+)?([eE][-+]?\d+)?\)
koniec linii                    L -> \n|\r|\r\n
```

W tym samym formacie zapisywana jest gramatyka wynikowa.

###### Uwagi
- Gramatyka musi zawierać symbol startowy
- Jeżeli nie podano prawdopodobieństwa reguły, zostanie ono przypisane automatycznie (patrz *Konfiguracja*)

###### Przykład
Gramatyka zdefiniowana następująco
```
Adv -> 'a' (0.345678)
Noun -> "b"
Verb -> Adv Noun (0.5)
VA -> Verb Adv (0.7)
NV -> Noun Verb
NVA -> Noun VA
S -> Adv NVA
```
zawiera:
- symbole terminalne `a, b`
- symbole nieterminalne `Adv, Noun, Verb, VA, NV, NVA`
- symbol startowy `S`
- reguły terminalne `Adv -> 'a', Noun -> 'b'`
- reguły nieterminalne `Verb -> Adv Noun, VA -> Verb Adv, NV -> Noun Verb, NVA -> Noun VA, S -> Adv NVA`

#### Baza sekwencji
Przykładowe zdania należy podawać w pliku tekstowym. Każde zdanie powinno znajdować się w osobnej linii i mieć następujący format:
```
<CORRECT> <PARAM> <TOKENS>
```
- `CORRECT` - `1` dla zdań poprawnych, `0` dla niepoprawnych
- `PARAM` - parametr pozostawiony dla zgodności z PyGCS; ignorowany
- `TOKENS` - sekwencja symboli terminalnych oddzielonych spacją

##### Przykład
Baza zdefiniowana następująco
```
1 3 a b c
0 7 c c c c c d e
```
zawiera:
- zdanie pozytywne `abc`
- zdanie negatywne `cccccde`

#### Konfiguracja
Plik konfiguracyjny musi być w formacie *properties*. Dostępne parametry opisano w przykładzie.
##### Przykład
```properties
#
# Grammar
#

## Jeżeli inicjalna gramatyka zawiera zduplikowane reguły, ustawienie false
## spowoduje zgłoszenie błędu, natomiast true - kontynuację wykonania.
## Obowiązująca będzie wówczas ostatnia definicja reguły.
grammar.skipDuplicates=true

## Jeżeli reguła nie posiada nadanego prawdopodobieństwa inicjalnego,
## zostanie ono ustawione automatycznie. W przypadku ustawienia true
## prawdopodobieństwa zostaną wylosowane, w innym wypadku reguły otrzymają 
## prawdopodobieńśtwo 1.
##
## Zarówno prawdopodobieństwa nadane ręcznie, jak i automatycznie
## są normalizowane na początku procesu.
grammar.randomProbabilities=true

## Jeżeli nie podano inicjalnej gramatyki zostanie ona wygenerowana losowo
## na podstawie wprowadzonego zbioru danych. Symbole i reguły terminalne będą
## odpowiadały symbolom znalezionym w zbiorze danych, ale ich liczba nie będzie
## większa niż podana w parametrze grammar.maxRandomTerminals (zostaną wylosowane
## spośród dostępnych). Reguły nieterminalne będą stanowiły wariacje utworzone
## z symboli nieterminalnych reguł terminalnych oraz symbolu startowego '$'.
## Ich liczba będzie równa wartości parametru grammar.randomNonTerminals. Jeśli
## możliwych wariacji symbolu będzie zbyt mało program zakończy się błędem.
grammar.maxRandomTerminals=10
grammar.randomNonTerminals=10

## Format wyjściowy i wejściowy gramatyki. Szczegóły opisano w sekcji "Gramatyka".
## SIMPLE - jednoznakowe symbole; białe znaki ignorowane
## EXTENDED - wieloznakowe symbole oddzielane spacją
grammar.format=SIMPLE

#
# CYK
#

## Liczba wątków, które mają zostać użyte do wyliczania CYK
cyk.numOfThreads=4

## Bazowy próg poprawności zdania - jest skalowany wg długości zdania
cyk.parsingThreshold=0.01

#
# Contrastive Estimation
#

## Maksymalny rozmiar sąsiedztwa
ce.maxNeighbourhoodSize=10

## Jeżeli true, sąsiedzi będą losowani spośród potencjalnych sąsiadów,
## jeśli ich liczba przekracza maksymalny rozmiar sąsiedztwa (wariant
## zalecany). W innym wypadku zostaną zawsze wybrane pierwsze elementy
## z listy potencjalnych sąsiadów (np. zawsze pierwsze 10 elementów).
ce.randomNeighbours=true

## Jeżeli true, wykonywanie indukcji dla danego zbioru danych zakończy
## się błędem, w wypadku gdy nie zostaną znalezieni żadni sąsiedzi
## dla żadnego zdania w zbiorze dla odmiany algorytmu innej niż IO.
ce.requireNeighbours=false

## Maksymalna liczba iteracji w jednym powtórzeniu
ce.iterations=150

## Współczynnik, przez który zostaje przemnożona wartość zmiany
## prawdopodobieństwa reguły, 1 oznacza przyjęcie wartości
## obliczonej przez algorytm (wariant zalecany).
ce.velocityFactor=0.25

## Odmiana algorytmu
## IO - algorytm InsideOutside
## CE_ALL - algorytm CE z sąsiedztwem wszystkich zdań negatywnych,
## CE_SAME_LENGTH - algorytm CE z sąsiedztwem zdań negatywnych podobnej długości
## CE_LEVENSHTEIN - algorytm CE z sąsiedztwem zdań o odległości Levenshteina (na poziomie wyrazów)
## nie wyższej niż podany distanceLimit
## CE_DAMERAU - algorytm CE z sąsiedztwem zdań o odległości Damerau-Levenshteina (na poziomie wyrazów)
## nie wyższej niż podany distanceLimit
## CE_JACCARD - algorytm CE z sąsiedztwem zdań o odległości Jaccarda (na poziomie wyrazów)
## nie wyższej niż podany distanceLimit
ce.mode=IO
ce.distanceLimit=3

## Jeżeli true, wartości inside zostaną obliczone w ramach algorytmu CYK (wariant polecany)
ce.calculateInsidesInCyk=true

## Tryb obliczania wartości outside.
## SEQUENTIAL - sekwencyjny (polecany)
## CELL_CONCURRENT - współbieżny - lista priorytetowa komórek 
## CELL_RULE_CONCURRENT - współbieżny - priorytetyzacja CellRules w wierszach
## Tryby współbieżne wymagają podania liczby wątków
ce.outsideMode=SEQUENTIAL
ce.numOfThreads=4

#
# Evaluation
#

## Liczba iteracji pomiędzy ewaluacjami. W przypadku wartości 0,
## ewaluacja zostanie przeprowadzona jedynie na końcu każdego wykonania.
ev.step=10

## Maksymalizowana funkcja. Algorytm zwróci na koniec gramatykę o najwyższej
## wartości podanego parametru spośród ewaluowanych (zgodnie z parametrem ev.step).
## SPECIFICITY, SENSITIVITY, PRECISION, F1
ev.maximizationTarget=F1

#
# Stop Condition
#
## Konfiguracja warunków stopu algorytmu. Warunki stopu są sprawdzane przy okazji
## ewaluacji, a więc zgodnie z parameterm ev.step. Dostępne są dwa warunki opisane
## poniżej. W przypadku obecności obu, wystarczy wsytąpienie jednego do zatrzymania
## algorytmu. Warunek można wyłączyć przez zakomentowanie jego zmiennych konfiguracyjnych.
## W przypadku błędnej konfiguracji warunek jest ignorowany - należy sprawdzić, czy na
## początku działania aplikacji warunki zostały poprawnie zainicjalizowane.

## Algorytm zostanie zatrzymany, jeśli parametry przekroczą podane wartości,
## oba parametry muszą być podane.
sc.expectedSensitvity=1
sc.expectedSpecificity=1

## Algorytm zostanie zatrzymany, jeżeli wartość parametru podanego jako ev.maximizationTarget
## zmniejszy się o podaną wartość w podanej liczbie ostatnich ewaluacji. Tzn. jeśli
## ev.step wynosi 10, a sc.mtDecreaseInSteps 5, to będzie sprawdzana różnica w 50 ostatnich
## iteracjach. Oba parametry muszą być podane.
sc.expectedMtDecrease=0.1
sc.mtDecreaseInSteps=5
```
