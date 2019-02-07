package io.grammar.token;

import grammar.Symbol;
import grammar.SymbolType;
import io.grammar.parser.AgContext;
import io.grammar.parser.AgInterpretable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.grammar.tokenType.AgTokenType.NON_TERMINAL_SYMBOL;

public abstract class AgNonTerminalSymbol extends AgToken implements AgInterpretable<Symbol> {

    private final String startSymbol;

    public AgNonTerminalSymbol(String value, String startSymbol) {
        super(value, NON_TERMINAL_SYMBOL);
        this.startSymbol = startSymbol;
    }

    public AgNonTerminalSymbol(Symbol symbol, String startSymbol) {
        super(symbol.getValue(), NON_TERMINAL_SYMBOL);
        this.startSymbol = startSymbol;
    }

    public Symbol interpret(AgContext context) {
        return context.getNonTerminalSymbols().computeIfAbsent(value, s -> {
            SymbolType type = SymbolType.NON_TERMINAL;
            Matcher matcher = Pattern.compile(startSymbol).matcher(value);
            if (matcher.matches())
                type = SymbolType.START;
            return new Symbol(value, context.nextSymbolIndex(), type);
        });
    }

}
