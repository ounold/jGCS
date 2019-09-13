package fitness;

import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykResult;
import dataset.Sequence;
import executionTime.ExecutionTimeService;
import grammar.Grammar;
import rulesTable.RulesTable;

public class FitnessService {

    private static final String POSITIVE_WEIGHT = "fitness.positiveWeight";
    private static final String NEGATIVE_WEIGHT = "fitness.negativeWeight";

    private ExecutionTimeService executionTimeService = ExecutionTimeService.getInstance();
    private Configuration configuration = ConfigurationService.getConfiguration();

    private static FitnessService instance;

    private FitnessService() {
    }

    public static FitnessService getInstance() {
        if (instance == null)
            instance = new FitnessService();
        return instance;
    }

    public void countRulesFitness(Grammar grammar) {
        grammar.getNonTerminalRules().forEach(rule -> {
            double fitness = 0.0;
            double positiveWeight = configuration.getDouble(POSITIVE_WEIGHT);
            double negativeWeight = configuration.getDouble(NEGATIVE_WEIGHT);
            if (((double) rule.getCountUsageInValidSentencesParsing() +  (double) rule.getCountUsageInNotValidSentencesParsing()) > 0) { //todo: refactor
                fitness = (rule.getCountUsageInValidSentencesParsing() * positiveWeight)
                        / ((double) rule.getCountUsageInValidSentencesParsing() * positiveWeight + (double) rule.getCountUsageInNotValidSentencesParsing() * negativeWeight);
            }
            rule.setFitness(fitness);
        });

    }

    public void countRulesUsage(Sequence sequence, CykResult cykResult, Grammar grammar) {
        RulesTable rulesTable = cykResult.getRulesTable();

        grammar.getNonTerminalRules().forEach(rule -> {
            for (int i = 1; i < rulesTable.getLength() - 1; i++) {
                for (int j = 0; j < rulesTable.getLength() - i; j++) {
                    if (rulesTable.getCellRule(i, j, rule) != null) {
                        if (sequence.isPositive()) {
                            rule.incrementUsageInValidSentencesParsing();
                        }
                        if (!sequence.isPositive()) {
                            rule.incrementUsageInNotValidSentencesParsing();
                        }
                    }
                }
            }
        });
    }
}
