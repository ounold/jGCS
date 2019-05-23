package covering;

import grammar.Grammar;
import grammar.Symbol;

import java.util.List;
import java.util.Random;

public abstract class Operator implements CoveringOperator {
    private Random random;

    public Operator() {
        random = new Random();
    }

    protected Symbol getRandomNonTerminalSymbol(Grammar grammar) {
        List<Symbol> nonTerminalSymbols = grammar.getNonTerminalSymbols();
        return nonTerminalSymbols.get(random.nextInt(nonTerminalSymbols.size()));
    }
}
