package io.grammar.parser;

public interface AgInterpretable<T> {

    T interpret(AgContext context);

}
