package io.grammar.rule.simple;

import grammar.Rule;
import io.grammar.rule.AgTerminalRule;
import io.grammar.token.*;
import io.grammar.token.simple.SimpleAgNonTerminalSymbol;
import io.grammar.token.simple.SimpleAgTerminalSymbol;

import java.util.LinkedList;

public class SimpleAgTerminalRule extends AgTerminalRule {

    public SimpleAgTerminalRule(Rule rule) {
        super(
                new SimpleAgNonTerminalSymbol(rule.getLeft()),
                new SimpleAgTerminalSymbol(rule.getRight1()),
                new AgProbability(rule.getProbability())
        );
    }

    public SimpleAgTerminalRule(AgNonTerminalSymbol left, AgTerminalSymbol right, AgProbability probability) {
        super(left, right, probability);
    }

    @Override
    public LinkedList<AgToken> write() {
        LinkedList<AgToken> result = new LinkedList<>();
        result.add(left);
        result.add(new AgArrow());
        result.add(right);
        result.add(probability);
        return result;
    }

}
