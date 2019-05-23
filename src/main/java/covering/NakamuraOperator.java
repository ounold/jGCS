package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class NakamuraOperator extends Operator {

    @Override
    public List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2) {
        Symbol randomSymbol = getRandomNonTerminalSymbol(grammar);
        Rule newRule = new Rule(randomSymbol, rightSymbol1, rightSymbol2, 0.0); //todo: probability
        List<Rule> newRules = new ArrayList<>();
        newRules.add(newRule);

        while (!isRuleEffective(newRule, grammar)) {
            Symbol randomSymbol1 = getRandomNonTerminalSymbol(grammar);
            Symbol randomSymbol2 = getRandomNonTerminalSymbol(grammar);
            newRule = new Rule(randomSymbol1, newRule.getLeft(), randomSymbol2, 0.0); //todo: prob
            newRules.add(newRule);
        }

        return newRules;
    }

    private boolean isRuleEffective(Rule newRule, Grammar grammar) {
        final Symbol newRuleLeft = newRule.getLeft();
        List<Rule> rules = grammar.getRules();

        return rules
                .stream()
                .anyMatch(rule -> rule.getRight1().equals(newRuleLeft) ||
                                    rule.getRight2() != null && rule.getRight2().equals(newRuleLeft));
    }
}
