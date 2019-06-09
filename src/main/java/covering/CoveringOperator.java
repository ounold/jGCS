package covering;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.List;

public interface CoveringOperator {
    List<Rule> generateRule(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol);
}
