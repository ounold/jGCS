package io.grammar.token.extended;

import io.grammar.token.AgToken;

import static io.grammar.tokenType.AgTokenType.TOKEN_SEPARATOR;

public class ExtendedAgTokenSeparator extends AgToken {

    public ExtendedAgTokenSeparator(){
        super(" ", TOKEN_SEPARATOR);
    }

    public ExtendedAgTokenSeparator(String value) {
        super(value, TOKEN_SEPARATOR);
    }

}
