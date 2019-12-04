package io.grammar.tokenType;

import io.grammar.token.AgToken;
import lombok.Getter;

import java.util.function.Function;

public class AgTokenTypeDefinition {

    @Getter
    private final AgTokenType tokenType;

    @Getter
    private final String definition;

    private final Function<String, AgToken> constructor;

    public AgTokenTypeDefinition(AgTokenType tokenType, String definition, Function<String, AgToken> constructor) {
        this.tokenType = tokenType;
        this.definition = definition;
        this.constructor = constructor;
    }

    public AgToken createInstance(String value){
        return constructor.apply(value);
    }

}
