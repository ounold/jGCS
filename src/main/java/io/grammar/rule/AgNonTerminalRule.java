package io.grammar.rule;

import grammar.Rule;
import io.grammar.parser.AgContext;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;

public abstract class AgNonTerminalRule implements AgRule {

    protected final AgNonTerminalSymbol left;

    protected final AgNonTerminalSymbol right1;

    protected final AgNonTerminalSymbol right2;

    protected final AgProbability probability;

    public AgNonTerminalRule(AgNonTerminalSymbol left, AgNonTerminalSymbol right1, AgNonTerminalSymbol right2, AgProbability probability) {
        this.left = left;
        this.right1 = right1;
        this.right2 = right2;
        this.probability = probability;
    }

    @Override
    public Rule interpret(AgContext context) {
        return context.saveNonTerminalRule(new Rule(left.interpret(context), right1.interpret(context), right2.interpret(context), probability.interpret(context)));
    }

}
