package io.grammar.rule.simple;

import grammar.Rule;
import io.grammar.rule.AgNonTerminalRule;
import io.grammar.token.AgArrow;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;
import io.grammar.token.AgToken;
import io.grammar.token.simple.SimpleAgNonTerminalSymbol;

import java.util.LinkedList;

public class SimpleAgNonTerminalRule extends AgNonTerminalRule {

    public SimpleAgNonTerminalRule(Rule rule){
        super(
                new SimpleAgNonTerminalSymbol(rule.getLeft()),
                new SimpleAgNonTerminalSymbol(rule.getRight1()),
                new SimpleAgNonTerminalSymbol(rule.getRight2()),
                new AgProbability(rule.getProbability())
        );
    }

    public SimpleAgNonTerminalRule(AgNonTerminalSymbol left, AgNonTerminalSymbol right1, AgNonTerminalSymbol right2, AgProbability probability) {
        super(left, right1, right2, probability);
    }

    @Override
    public LinkedList<AgToken> write() {
        LinkedList<AgToken> result = new LinkedList<>();
        result.add(left);
        result.add(new AgArrow());
        result.add(right1);
        result.add(right2);
        result.add(probability);
        return result;
    }

}
