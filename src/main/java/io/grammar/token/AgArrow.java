package io.grammar.token;

import io.grammar.tokenType.AgTokenType;

public class AgArrow extends AgToken {

    public AgArrow(){
        super("->", AgTokenType.ARROW);
    }

    public AgArrow(String value) {
        super(value, AgTokenType.ARROW);
    }
}
