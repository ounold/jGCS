package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.Collections;
import java.util.List;

public class AggressiveOperator extends Operator {
    @Override
    public List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol) {
        Symbol randomSymbol = getRandomNonTerminalSymbolWithoutStart(grammar);
        Rule rule = new Rule(randomSymbol, rightSymbol1, rightSymbol2, 1.0);
        return Collections.singletonList(rule);
    }
}
