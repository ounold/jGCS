package io.grammar.token;

import grammar.Symbol;
import io.grammar.parser.AgInterpretable;

import static io.grammar.tokenType.AgTokenType.TERMINAL_SYMBOL;

public abstract class AgTerminalSymbol extends AgToken implements AgInterpretable<Symbol> {

    public AgTerminalSymbol(String value) {
        super(value, TERMINAL_SYMBOL);
    }

}
