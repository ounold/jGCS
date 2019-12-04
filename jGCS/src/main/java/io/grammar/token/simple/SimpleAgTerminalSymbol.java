package io.grammar.token.simple;

import grammar.Symbol;
import grammar.SymbolType;
import io.grammar.parser.AgContext;
import io.grammar.token.AgTerminalSymbol;

public class SimpleAgTerminalSymbol extends AgTerminalSymbol {

    public SimpleAgTerminalSymbol(String value) {
        super(value);
    }

    public SimpleAgTerminalSymbol(Symbol symbol) {
        super(symbol.getValue());
    }

    @Override
    public Symbol interpret(AgContext context) {
        return context.getTerminalSymbols().computeIfAbsent(value,
                s -> new Symbol(value, 0, SymbolType.TERMINAL));
    }

}
