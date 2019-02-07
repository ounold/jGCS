package probabilityArray;

import grammar.Symbol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProbabilityArray {

    private ProbabilityCell[][][] table;

    public ProbabilityArray(int sentenceLength, int nonTerminalSymbolsCount) {
        table = new ProbabilityCell[sentenceLength][sentenceLength][nonTerminalSymbolsCount];
        for (int i = 0; i < sentenceLength; i++) {
            for (int j = 0; j < sentenceLength - i; j++) {
                for (int k = 0; k < nonTerminalSymbolsCount; k++) {
                    table[i][j][k] = ProbabilityCell.EMPTY_CELL;
                }
            }
        }
    }

    public ProbabilityCell get(int i, int j, int k) {
        return table[i][j][k];
    }

    public ProbabilityCell get(int i, int j, Symbol symbol) {
        return table[i][j][symbol.getIndex()];
    }

    public synchronized ProbabilityCell add(int i, int j, Symbol symbol, double value) {
        if (table[i][j][symbol.getIndex()] == ProbabilityCell.EMPTY_CELL)
            table[i][j][symbol.getIndex()] = new ProbabilityCell(value);
        else
            table[i][j][symbol.getIndex()].add(value);
        return table[i][j][symbol.getIndex()];
    }

    public ProbabilityCell getStartCell(Symbol symbol) {
        return get(table.length - 1, 0, symbol);
    }
}
