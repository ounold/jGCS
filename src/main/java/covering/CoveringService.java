package covering;

import application.ApplicationException;
import configuration.ConfigurationService;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.List;

public class CoveringService {

    private static final String COVERING_OPERATOR = "covering.operator";

    private static CoveringService instance;

    private final CoveringOperator coveringOperator;
    private final CoveringOperatorType operatorType;

    public CoveringService() {
        this.operatorType = ConfigurationService.getConfiguration().getEnum(CoveringOperatorType::valueOf, COVERING_OPERATOR);
        this.coveringOperator = initOperator();
    }

    public static CoveringService getInstance() {
        if (instance == null) {
            instance = new CoveringService();
        }
        return instance;
    }

    public List<Rule> run(Grammar grammar, Symbol rightSymbol1, Symbol rightSymbol2) {
        List<Rule> rules = coveringOperator.generateRule(grammar, rightSymbol1, rightSymbol2);
        return rules;
    }


    private CoveringOperator initOperator() {
        switch (operatorType) {
            case STANDARD:
                return new StandardOperator();
            case NAKAMURA:
                return new NakamuraOperator();
            default:
                throw new ApplicationException("Covering operator unknown");
        }
    }
}
