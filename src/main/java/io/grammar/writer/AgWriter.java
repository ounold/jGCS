package io.grammar.writer;

import grammar.Grammar;

public abstract class AgWriter {

    protected final Grammar grammar;

    public AgWriter(Grammar grammar) {
        this.grammar = grammar;
    }

    public abstract String writeGrammar();

}
