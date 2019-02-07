package io.grammar.rule.extended;

import grammar.Rule;
import io.grammar.rule.AgTerminalRule;
import io.grammar.token.*;
import io.grammar.token.extended.ExtendedAgNonTerminalSymbol;
import io.grammar.token.extended.ExtendedAgTerminalSymbol;
import io.grammar.token.extended.ExtendedAgTokenSeparator;

import java.util.LinkedList;

public class ExtendedAgTerminalRule extends AgTerminalRule {

    public ExtendedAgTerminalRule(Rule rule){
        super(
                new ExtendedAgNonTerminalSymbol(rule.getLeft()),
                new ExtendedAgTerminalSymbol(rule.getRight1()),
                new AgProbability(rule.getProbability())
        );
    }

    public ExtendedAgTerminalRule(AgNonTerminalSymbol left, AgTerminalSymbol right, AgProbability probability) {
        super(left, right, probability);
    }

    @Override
    public LinkedList<AgToken> write() {
        LinkedList<AgToken> result = new LinkedList<>();
        result.add(left);
        result.add(new ExtendedAgTokenSeparator());
        result.add(new AgArrow());
        result.add(new ExtendedAgTokenSeparator());
        result.add(right);
        result.add(new ExtendedAgTokenSeparator());
        result.add(probability);
        return result;
    }

}
