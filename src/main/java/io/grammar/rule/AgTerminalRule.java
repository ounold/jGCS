package io.grammar.rule;

import grammar.Rule;
import io.grammar.parser.AgContext;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;
import io.grammar.token.AgTerminalSymbol;

public abstract class AgTerminalRule implements AgRule {

    protected final AgNonTerminalSymbol left;

    protected final AgTerminalSymbol right;

    protected final AgProbability probability;

    public AgTerminalRule(AgNonTerminalSymbol left, AgTerminalSymbol right, AgProbability probability) {
        this.left = left;
        this.right = right;
        this.probability = probability;
    }

    @Override
    public Rule interpret(AgContext context) {
        return context.saveTerminalRule(new Rule(left.interpret(context), right.interpret(context), null, probability.interpret(context)));
    }

}
