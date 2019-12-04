package covering;

import grammar.Grammar;
import grammar.Symbol;
import grammar.SymbolType;

import java.util.List;
import java.util.Random;

abstract class Operator implements CoveringOperator {
    private Random random;

    Operator() {
        random = new Random();
    }

    Symbol getRandomNonTerminalSymbol(Grammar grammar) {
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        return nonTerminalSymbols.get(random.nextInt(nonTerminalSymbols.size()));
    }

    Symbol getRandomNonTerminalSymbolWithoutStart(Grammar grammar) {
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        Symbol symbol = nonTerminalSymbols.get(random.nextInt(nonTerminalSymbols.size()));
        while (symbol.getSymbolType().equals(SymbolType.START)) {
            symbol = nonTerminalSymbols.get(random.nextInt(nonTerminalSymbols.size()));
        }
        return symbol;
    }
}
