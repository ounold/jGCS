package io.grammar.token.extended;

import grammar.Symbol;
import grammar.SymbolType;
import io.grammar.parser.AgContext;
import io.grammar.token.AgTerminalSymbol;

public class ExtendedAgTerminalSymbol extends AgTerminalSymbol {

    public ExtendedAgTerminalSymbol(String value) {
        super(value);
    }

    public ExtendedAgTerminalSymbol(Symbol symbol) {
        super(withQutationMarks(symbol.getValue()));
    }

    @Override
    public Symbol interpret(AgContext context) {
        return context.getTerminalSymbols().computeIfAbsent(value,
                s -> new Symbol(value.substring(1, value.length() - 1), 0, SymbolType.TERMINAL));
    }

    private static String withQutationMarks(String value) {
        if (value.contains("'"))
            return "\"" + value + "\"";
        else
            return "'" + value + "'";
    }

}
