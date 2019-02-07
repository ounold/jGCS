package io.grammar.token.extended;

import grammar.Symbol;
import io.grammar.token.AgNonTerminalSymbol;

public class ExtendedAgNonTerminalSymbol extends AgNonTerminalSymbol {

    private static final String START_SYMBOL = "S";

    public ExtendedAgNonTerminalSymbol(String value) {
        super(value, START_SYMBOL);
    }

    public ExtendedAgNonTerminalSymbol(Symbol symbol) {
        super(symbol.getValue(), START_SYMBOL);
    }
}
