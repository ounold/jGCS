package io.grammar.token;

import io.grammar.tokenType.AgTokenType;
import lombok.Getter;

public abstract class AgToken {

    @Getter
    protected final String value;

    @Getter
    private final AgTokenType type;

    public AgToken(String value, AgTokenType type) {
        this.value = value;
        this.type = type;
    }

    public String write(){
        return value;
    }
}
