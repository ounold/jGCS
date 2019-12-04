1. Zbiory treningowy zapisać w katalogu jGCS/bin/longDatasets/trainDatasets
2. Zbiory testowy zapisać w katalogu jGCS/bin/longDatasets/testDatasets
3. Zedytować plik run.bat:

	java -jar JGCS.jar -d longDatasets/trainDatasets/<ZBIOR_TRENINGOWY>.txt -v longDatasets/testDatasets/<ZBIOR_TESTOWY>.txt -e set_<NUMER_ZBIORU>.csv -o grammar_<NUMER_ZBIORU>  -r 10 -c config.properties > <NUMER_ZBIORU>_logs.txt

	Podmienić odpowiednio pola ze znacznikami <>: <ZBIOR_TRENINGOWY>, <ZBIOR_TESTOWY>, <NUMER_ZBIORU> # ZNACZNIKI <> USUWAMY!

	Np. W folderze trainDatasets mamy "przyklad_train_1", a w testowym "przyklad_test_1":

	java -jar JGCS.jar -d longDatasets/trainDatasets/przyklad_train_1.txt -v longDatasets/testDatasets/przyklad_test_1.txt -e set_1.csv -o grammar_1  -r 10 -c config.properties > wyniki/1_logs.txt

	Proszę podmienić 7 zbiorów. Np. od 1-7 dla prof. Bożejki, od 8-14 dla prof. Unolda

	Obecnie w folderze trainDatasets i testDatasets jest po jednym pliku. Można przetestować działanie.

4. Uruchomić plik run.bat