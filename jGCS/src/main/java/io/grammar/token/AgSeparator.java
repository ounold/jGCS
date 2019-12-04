package io.grammar.token;

import static io.grammar.tokenType.AgTokenType.SEPARATOR;

public abstract class AgSeparator extends AgToken {

    public AgSeparator(String value) {
        super(value, SEPARATOR);
    }

}
