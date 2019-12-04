package fitness;

import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykResult;
import dataset.Sequence;
import executionTime.ExecutionTimeService;
import grammar.Grammar;
import rulesTable.CellRule;
import rulesTable.RulesTable;

public class FitnessService {

    private static final String POSITIVE_WEIGHT = "fitness.positiveWeight";
    private static final String NEGATIVE_WEIGHT = "fitness.negativeWeight";
    private static final String NON_USED_RULE_FITNESS = "fitness.nonUsedRuleFitness";
    private static final String CLASSIC_WEIGHT = "fitness.classicWeight";
    private static final String FERTILITY_WEIGHT = "fitness.fertilityWeight";
    private static final String RULE_BASE_AMOUNT = "rules.baseAmount";
    private static final String RULE_BASE_AMOUNT_REDUCTION_COEFFICIENT = "rules.baseAmountReductionCoefficient";

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
            if (((double) rule.getCountUsageInValidSentencesParsing() + (double) rule.getCountUsageInNotValidSentencesParsing()) > 0) { //todo: refactor
                fitness = (rule.getCountUsageInValidSentencesParsing() * positiveWeight)
                        / ((double) rule.getCountUsageInValidSentencesParsing() * positiveWeight + (double) rule.getCountUsageInNotValidSentencesParsing() * negativeWeight);
            }
            rule.setFitness(fitness);
        });

    }

    public void countClassicRulesFitness(Grammar grammar) {
        final double[] ffMax = {Double.MIN_VALUE};
        final double[] ffMin = {Double.MAX_VALUE};
        double nonUsedRuleFitness = configuration.getDouble(NON_USED_RULE_FITNESS);
        double positiveWeight = configuration.getDouble(POSITIVE_WEIGHT);
        double negativeWeight = configuration.getDouble(NEGATIVE_WEIGHT);
        double classicWeight = configuration.getDouble(CLASSIC_WEIGHT);
        double fertilityWeight = configuration.getDouble(FERTILITY_WEIGHT);

//        System.out.println("FFMAX : " + ffMax[0]);
//        System.out.println("FFMIN : " + ffMin[0]);

        grammar.getRules()
                .stream()
                .forEach(rule -> {
            double ruleFFValue = rule.getProfit() - rule.getDebt();
            if (ruleFFValue > ffMax[0]) {
                ffMax[0] = ruleFFValue;
            }
            if (ruleFFValue < ffMin[0]) {
                ffMin[0] = ruleFFValue;
            }
//                    System.out.println("RULE FFVALUE: " + ruleFFValue);


        });



//        for (int i = 0; i < grammar.getRules().size(); i++) {
//            double ruleFFValue = grammar.getRules().get(i).getProfit() - grammar.getRules().get(i).getDebt();
//            if (ruleFFValue > ffMax) {
//                ffMax = ruleFFValue;
//            }
//            if (ruleFFValue < ffMin) {
//                ffMin = ruleFFValue;
//            }
//
//        }

        grammar.getRules()
                .stream()
                .forEach(rule -> {
                    double fc;
                    if (rule.getCountUsageInValidSentencesParsing() == 0 && rule.getCountUsageInNotValidSentencesParsing() == 0) {
                        fc = nonUsedRuleFitness;
                    } else {
                        fc = (positiveWeight * rule.getCountUsageInValidSentencesParsing()) /
                                (positiveWeight * rule.getCountUsageInValidSentencesParsing() + negativeWeight * rule.getCountUsageInNotValidSentencesParsing());
                    }

                    double ff = 0.0;

                    if ((ffMax[0] - ffMin[0]) != 0) {
                        ff = (rule.getProfit() - rule.getDebt() - ffMin[0]) / (ffMax[0] - ffMin[0]);
                    }
//                    System.out.println("FFMAX END: " + ffMax[0]);
//                    System.out.println("FFMIN END: " + ffMin[0]);

//                    System.out.println("PROFIT: " + rule.getProfit());
//                    System.out.println("DEBT: " + rule.getDebt());
//                    System.out.println("FERTILITY: " + ff);
                    rule.setFertility(ff);

                    double fitness = (classicWeight * fc + fertilityWeight * ff) /
                            (classicWeight + fertilityWeight);

//                    System.out.println("FITNESS: " + fitness);
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

    public void resetRulesUsagesDebtsProfits(Grammar grammar) {
        grammar.getRules()
                .stream()
                .forEach(rule -> {
                    rule.setCountUsageInValidSentencesParsing(0);
                    rule.setCountUsageInNotValidSentencesParsing(0);
                    rule.setProfit(0);
                    rule.setDebt(0);
                });
    }

    public void calculateRulesValues(int sentenceLength, RulesTable rulesTable) {
        for (int i = 1; i < sentenceLength; i++) {
            for (int j = 0; j < sentenceLength - i; j++) {
                int cellRulesLength = rulesTable.get(i, j).getCellRules().size();
                for (int k = 0; k < i; k++) {
                    int parent1CellRulesLength = rulesTable.get(k, j).getCellRules().size();
                    int parent2CellRulesLength = rulesTable.get(i - k - 1, j + k + 1).getCellRules().size();

                    for (int cellRuleIndex = 0; cellRuleIndex < cellRulesLength; cellRuleIndex++) {
                        int parent1 = -1;
                        int parent2 = -1;

                        for (int parentRuleIndex = 0; parentRuleIndex < parent1CellRulesLength; parentRuleIndex++) {
                            if (rulesTable.get(k, j).getCellRules().get(parentRuleIndex).getRule().getLeft().getValue() ==
                                    rulesTable.get(i, j).getCellRules().get(cellRuleIndex).getRule().getRight1().getValue()) {
                                parent1 = parentRuleIndex;
                            }
                        }
                        for (int parentRuleIndex = 0; parentRuleIndex < parent2CellRulesLength; parentRuleIndex++) {
                            if (rulesTable.get(i, j).getCellRules().get(cellRuleIndex).getRule().getRight2().getValue() != null &&
                                    rulesTable.get(i - k - 1, j + k + 1).getCellRules().get(parentRuleIndex).getRule().getLeft().getValue() ==
                                            rulesTable.get(i, j).getCellRules().get(cellRuleIndex).getRule().getRight2().getValue()) {
                                parent2 = parentRuleIndex;
                            }
                        }

                        if (parent1 != -1 && parent2 != -1) {

                            double newTmp = computeNewTmpValue(rulesTable.get(k, j).getCellRules().get(parent1),
                                    rulesTable.get(i - k - 1, j + k + 1).getCellRules().get(parent2)
                            );
                            rulesTable.get(i, j).getCellRules().get(cellRuleIndex).setTmpVal(newTmp);
                        }

                    }
                }
            }
        }
    }

    private double computeNewTmpValue(CellRule cellRule1, CellRule cellRule2) {
        return cellRule1.getTmpVal() * configuration.getDouble(RULE_BASE_AMOUNT_REDUCTION_COEFFICIENT) +
                cellRule2.getTmpVal() * configuration.getDouble(RULE_BASE_AMOUNT_REDUCTION_COEFFICIENT);
    }

    public void updateRuleProfitAndDebt(Boolean positive, int sentenceLength, RulesTable rulesTable) {
        for (int i = 0; i < sentenceLength; i++) {
            for (int j = 0; j < sentenceLength - i; j++) {
                for (int k = 0; k < rulesTable.get(i, j).getCellRules().size(); k++) {
                    if (positive) {
                        double newProfit = rulesTable.get(i, j).getCellRules().get(k).getRule().getProfit()
                                + rulesTable.get(i, j).getCellRules().get(k).getTmpVal();
                        rulesTable.get(i, j).getCellRules().get(k).getRule().setProfit(newProfit);
                    } else {
                        double newDebt = rulesTable.get(i, j).getCellRules().get(k).getRule().getDebt()
                                + rulesTable.get(i, j).getCellRules().get(k).getTmpVal();
                        rulesTable.get(i, j).getCellRules().get(k).getRule().setDebt(newDebt);
                    }
                }
            }
        }
    }

    public void initFirstRowTmpValues(int sentenceLength, RulesTable rulesTable) {
        for (int i = 0; i < sentenceLength; i++) {
            for (int j = 0; j < rulesTable.get(0, i).getCellRules().size(); j++) {
                rulesTable.get(0, i).getCellRules().get(j).setTmpVal(configuration.getDouble(RULE_BASE_AMOUNT));
            }
        }
    }

    public void fillDebtsProfits(Sequence sentence, Boolean positive, CykResult cykResult) {
        RulesTable rulesTable = cykResult.getRulesTable();
        int sentenceLength = sentence.length();
        initFirstRowTmpValues(sentenceLength, rulesTable);
        calculateRulesValues(sentenceLength, rulesTable);
        updateRuleProfitAndDebt(positive, sentenceLength, rulesTable);

    }
}
