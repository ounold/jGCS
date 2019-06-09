package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressiveOperator extends Operator {

    private AtomicBoolean executed;

    public ProgressiveOperator() {
        executed = new AtomicBoolean(false);
    }

    @Override
    public List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol) {
        if (!executed.get() && isLastCell && !hasStartSymbol) {
            executed.set(true);
            return generateStartSymbolRule(grammar, rightSymbol1, rightSymbol2);
        }
        if (!executed.get()) {
            executed.set(true);
            return generateRule(grammar, rightSymbol1, rightSymbol2);
        }
        return Collections.emptyList();
    }

    private List<Rule> generateStartSymbolRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2) {
        Optional<Symbol> startSymbol = grammar.getNonTerminalSymbols().stream()
                .filter(symbol -> symbol.getSymbolType().equals(SymbolType.START))
                .findFirst();

        if (!startSymbol.isPresent()) {
            return Collections.emptyList();
        }
        Rule rule = new Rule(startSymbol.get(), rightSymbol1, rightSymbol2, 1.0);
        return Collections.singletonList(rule);
    }

    private List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2) {
        Symbol randomSymbol = getRandomNonTerminalSymbol(grammar);
        Rule rule = new Rule(randomSymbol, rightSymbol1, rightSymbol2, 1.0);
        return Collections.singletonList(rule);
    }
}
