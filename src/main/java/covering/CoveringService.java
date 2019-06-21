package covering;

import application.ApplicationException;
import configuration.ConfigurationService;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.List;

public class CoveringService {

    private static final String COVERING_OPERATOR = "covering.operator";

    private final CoveringOperator coveringOperator;
    private final FinalOperator finalOperator;
    private final CoveringOperatorType operatorType;

    public CoveringService() {
        this.operatorType = ConfigurationService.getConfiguration().getEnum(CoveringOperatorType::valueOf, COVERING_OPERATOR);
        this.coveringOperator = initOperator();
        this.finalOperator = new FinalOperator();
    }

    public List<Rule> run(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol) {
        if (operatorType.equals(CoveringOperatorType.PROGRESSIVE)) {
            return coveringOperator.generateRule(grammar, rightSymbol1, rightSymbol2, isLastCell, hasStartSymbol);
        }
        return runWithFinalOperator(grammar, rightSymbol1, rightSymbol2, isLastCell, hasStartSymbol);
    }

    private List<Rule> runWithFinalOperator(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2, boolean isLastCell, boolean hasStartSymbol) {
        if (isLastCell && !hasStartSymbol) {
            return finalOperator.generateRule(grammar, rightSymbol1, rightSymbol2, true, false);
        }
        return coveringOperator.generateRule(grammar, rightSymbol1, rightSymbol2, isLastCell, hasStartSymbol);
    }


    private CoveringOperator initOperator() {
        switch (operatorType) {
            case AGGRESSIVE:
                return new AggressiveOperator();
            case NAKAMURA:
                return new NakamuraOperator();
            case PROGRESSIVE:
                return new ProgressiveOperator();
            default:
                throw new ApplicationException("Covering operator unknown");
        }
    }
}
