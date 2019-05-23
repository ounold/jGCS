package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StandardOperator extends Operator {
    @Override
    public List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2) {
        Symbol randomSymbol = getRandomNonTerminalSymbol(grammar);
        Rule rule = new Rule(randomSymbol, rightSymbol1, rightSymbol2, 1.0);
        return Collections.singletonList(rule);
    }
}
