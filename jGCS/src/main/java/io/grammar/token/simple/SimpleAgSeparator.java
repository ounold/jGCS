package io.grammar.token.simple;

import io.grammar.token.AgSeparator;

public class SimpleAgSeparator extends AgSeparator {

    public SimpleAgSeparator(){
        super(";");
    }

    public SimpleAgSeparator(String value) {
        super(value);
    }
}
