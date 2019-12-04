package io.grammar.rule.extended;

import grammar.Rule;
import io.grammar.rule.AgNonTerminalRule;
import io.grammar.token.AgArrow;
import io.grammar.token.AgNonTerminalSymbol;
import io.grammar.token.AgProbability;
import io.grammar.token.AgToken;
import io.grammar.token.extended.ExtendedAgNonTerminalSymbol;
import io.grammar.token.extended.ExtendedAgTokenSeparator;

import java.util.LinkedList;

public class ExtendedAgNonTerminalRule extends AgNonTerminalRule {

    public ExtendedAgNonTerminalRule(Rule rule){
        super(
                new ExtendedAgNonTerminalSymbol(rule.getLeft()),
                new ExtendedAgNonTerminalSymbol(rule.getRight1()),
                new ExtendedAgNonTerminalSymbol(rule.getRight2()),
                new AgProbability(rule.getProbability())
        );
    }

    public ExtendedAgNonTerminalRule(AgNonTerminalSymbol left, AgNonTerminalSymbol right1, AgNonTerminalSymbol right2, AgProbability probability) {
        super(left, right1, right2, probability);
    }

    @Override
    public LinkedList<AgToken> write() {
        LinkedList<AgToken> result = new LinkedList<>();
        result.add(left);
        result.add(new ExtendedAgTokenSeparator());
        result.add(new AgArrow());
        result.add(new ExtendedAgTokenSeparator());
        result.add(right1);
        result.add(new ExtendedAgTokenSeparator());
        result.add(right2);
        result.add(new ExtendedAgTokenSeparator());
        result.add(probability);
        return result;
    }

}
