package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FinalOperator extends Operator {
    @Override
    public List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol) {
        Optional<Symbol> startSymbol = grammar.getNonTerminalSymbols().stream()
                .filter(symbol -> symbol.getSymbolType().equals(SymbolType.START))
                .findFirst();

        if (!startSymbol.isPresent()) {
            return Collections.emptyList();
        }
        Rule rule = new Rule(startSymbol.get(), rightSymbol1, rightSymbol2, 1.0);
        return Collections.singletonList(rule);
    }
}
