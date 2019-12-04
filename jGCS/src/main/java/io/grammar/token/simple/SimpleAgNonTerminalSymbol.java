package io.grammar.token.simple;

import grammar.Symbol;
import io.grammar.token.AgNonTerminalSymbol;

public class SimpleAgNonTerminalSymbol extends AgNonTerminalSymbol {

    private static final String START_SYMBOL = "\\$";

    public SimpleAgNonTerminalSymbol(String value) {
        super(value, START_SYMBOL);
    }

    public SimpleAgNonTerminalSymbol(Symbol symbol) {
        super(symbol.getValue(), START_SYMBOL);
    }

}
