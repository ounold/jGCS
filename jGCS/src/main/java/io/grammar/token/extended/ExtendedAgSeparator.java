package io.grammar.token.extended;

import io.grammar.token.AgSeparator;

public class ExtendedAgSeparator extends AgSeparator {

    public ExtendedAgSeparator(){
        super("\n");
    }

    public ExtendedAgSeparator(String value) {
        super(value);
    }
}
