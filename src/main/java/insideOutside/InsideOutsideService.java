package insideOutside;

import application.ApplicationException;
import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykNavigator;
import cyk.CykResult;
import executionTime.EtMarkers;
import executionTime.EtMarkersChain;
import executionTime.ExecutionTimeService;
import grammar.Grammar;
import grammar.Rule;
import induction.GrammarInductionService;
import induction.InductionMode;
import insideOutside.outside.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import probabilityArray.ProbabilityArray;
import rulesTable.CellRule;
import rulesTable.PureCellRule;
import rulesTable.RulesTable;

import java.util.ArrayList;
import java.util.List;

public class InsideOutsideService {

    private static final Logger LOGGER = LogManager.getLogger(InsideOutsideService.class);

    private static final String CALCULATE_INSIDES_IN_CYK = "ce.calculateInsidesInCyk";
    private static final String OUTSIDE_MODE = "ce.outsideMode";
    private static final String CE_MODE = "ce.mode";
    private static final String CE_VELOCITY_FACTOR = "ce.velocityFactor";

    public static final EtMarkersChain ETMC_INSIDE = GrammarInductionService.ETMC_IO_COUNTS.get(EtMarkers.ET_INSIDE);
    public static final EtMarkersChain ETMC_OUTSIDE = GrammarInductionService.ETMC_IO_COUNTS.get(EtMarkers.ET_OUTSIDE);
    public static final EtMarkersChain ETMC_VALUES = GrammarInductionService.ETMC_IO_COUNTS.get(EtMarkers.ET_INSIDE_OUTSIDE_VALUES);
    public static final EtMarkersChain ETMC_COUNTS = GrammarInductionService.ETMC_IO_COUNTS.get(EtMarkers.ET_COUNTS);

    private Configuration configuration = ConfigurationService.getConfiguration();

    private ExecutionTimeService executionTimeService = ExecutionTimeService.getInstance();

    private static InsideOutsideService instance;

    private InsideOutsideService() {
    }

    public static InsideOutsideService getInstance() {
        if (instance == null)
            instance = new InsideOutsideService();
        return instance;
    }

    public void updateRulesProbabilities(Grammar grammar) {
        InductionMode mode = configuration.getEnum(InductionMode::valueOf, CE_MODE);
        double velocityFactor = configuration.getDouble(CE_VELOCITY_FACTOR);
        grammar.getRules()
                .stream()
                .filter(r -> r.getPositiveCount() != 0 || r.getNegativeCount() != 0)
                .forEach(rule -> {
//                    LOGGER.info("Rule: {}", rule.toFullString());
                    double sumOfAllPositiveCountsWithLeftSymbol = sumAllPositiveCountsWithLeftSymbol(grammar, rule);
                    double newProbability = calculateNewProbability(rule, sumOfAllPositiveCountsWithLeftSymbol, mode);
                    if (newProbability != 1e-4) {
                        rule.setProbability(newProbability);
                    } else {
                        rule.setProbability(0);
                    }
//                    LOGGER.info("Rule: {}", rule.toFullString());
//                    LOGGER.info("Probability: {}", newProbability);
//                    LOGGER.info("CE: {}", ceFactor);
                });
    }

    private double sumAllPositiveCountsWithLeftSymbol(Grammar grammar, Rule rule) {
        return grammar.getRules().stream()
                .filter(r -> r.getLeft() == rule.getLeft())
                .mapToDouble(Rule::getPositiveCount)
                .sum();
    }

    private double calculateNewProbability(Rule rule, double sumAllPositiveCountsWithLeftSymbol, InductionMode mode) {
        double newProbability = 0;
        if (rule.getPositiveCount() != 0) {
            double denominator = sumAllPositiveCountsWithLeftSymbol;
            if (mode != InductionMode.IO)
                denominator += rule.getCountInPositives() + rule.getCountInNeighbourhoods();
            newProbability = rule.getPositiveCount() / denominator;
        }
        return newProbability;
    }

    public void updateRulesCounts(Grammar grammar, CykResult cykResult, IoSequence sequence) {
        if (!configuration.getBoolean(CALCULATE_INSIDES_IN_CYK))
            executionTimeService.saveExecutionTime(ETMC_INSIDE, () -> calculateInside(cykResult));
        executionTimeService.saveExecutionTime(ETMC_OUTSIDE, () -> calculateOutside(sequence, cykResult));
        executionTimeService.saveExecutionTime(ETMC_VALUES, () -> updateInsideOutsideValues(cykResult, sequence.isPositive()));
        executionTimeService.saveExecutionTime(ETMC_COUNTS, () -> updateCounts(grammar, cykResult.getSentenceProbability(), sequence));
    }

    public void resetInsideOutsideValuesAndCounts(Grammar grammar) {
        grammar.getRules().forEach(this::resetInsideOutsideValuesAndCounts);
    }

    public void resetInsideOutsideValues(Grammar grammar) {
        grammar.getRules().forEach(this::resetInsideOutsideValues);
    }

    public void updateCounts(Grammar grammar, double sentenceProbability, IoSequence sequence) {
        grammar.getRules().forEach(rule -> {
            LOGGER.debug("Calculating counts of {} with sentenceProbability {}", rule.getDefinition(), sentenceProbability);
            if (sentenceProbability > 0.0000000000001) {
                double relativeProbability = rule.getProbability() / sentenceProbability;
                double newCount = relativeProbability * rule.getSumInsideOutsideUsages();
                rule.addCount(newCount);
                rule.addPositiveCount(relativeProbability * rule.getPositiveSumInsideOutsideUsages());
                rule.addNegativeCount(relativeProbability * rule.getNegativeSumInsideOutsideUsages());
                rule.addCountInNeighbourhood(newCount * sequence.getTimesAsNeighbour());
                if (sequence.isPositive())
                    rule.addCountInPositives(newCount);
            }
        });
    }

    private void resetInsideOutsideValuesAndCounts(Rule rule) {
        rule.setCount(0);
        rule.setPositiveCount(0);
        rule.setNegativeCount(0);
        rule.setCountInNeighbourhoods(0);
        rule.setCountInPositives(0);
        resetInsideOutsideValues(rule);
    }

    private void resetInsideOutsideValues(Rule rule) {
        rule.setPositiveSumInsideOutsideUsages(0);
        rule.setNegativeSumInsideOutsideUsages(0);
        rule.setCountInsideOutsideUsageProbability(0);
    }

    private void updateInsideOutsideValues(CykResult cykResult, boolean positive) {
        RulesTable rulesTable = cykResult.getRulesTable();
        calculateInsideOutside(rulesTable, rulesTable.getStartCellRules(), positive);
    }

    private void calculateInsideOutside(RulesTable rulesTable, List<CellRule> cellRules, boolean positive) {
        if (cellRules.isEmpty())
            return;
        List<CellRule> newCellRules = new ArrayList<>();
        for (CellRule cellRule : cellRules) {
            if (cellRule.isCalculated())
                continue;
            cellRule.setCalculated(true);
            List<CellRule> right1Rules = rulesTable.findMatchingRules(cellRule.getCell1Coordinates(), cellRule.getRule().getRight1());
            List<CellRule> right2Rules = rulesTable.findMatchingRules(cellRule.getCell2Coordinates(), cellRule.getRule().getRight2());
            newCellRules.addAll(right1Rules);
            newCellRules.addAll(right2Rules);
            calculateInsideOutside(cellRule, right1Rules, right2Rules, positive);
        }
        calculateInsideOutside(rulesTable, newCellRules, positive);
    }

    private void calculateInsideOutside(CellRule cellRule, List<CellRule> right1Rules, List<CellRule> right2Rules, boolean positive) {
        if (!right1Rules.isEmpty() && !right2Rules.isEmpty())
            cellRule.getRule().addInsideOutsideUsages(cellRule.getOutside() * right1Rules.get(0).getInside() * right2Rules.get(0).getInside(), positive);
        else if (right1Rules.isEmpty() && right2Rules.isEmpty())
            cellRule.getRule().addInsideOutsideUsages(cellRule.getOutside(), positive);
    }

    private void calculateOutside(IoSequence sequence, CykResult cykResult) {
        OutsideProcessor processor = getOutsideProcessor();
        processor.calculateOutside(sequence, cykResult);
    }

    private OutsideProcessor getOutsideProcessor() {
        switch (configuration.getEnum(OutsideMode::valueOf, OUTSIDE_MODE)) {
            case CELL_CONCURRENT:
                return new CellConcurrentOutsideProcessor();
            case CELL_RULE_CONCURRENT:
                return new CellRuleConcurrentOutsideProcessor();
            case SEQUENTIAL:
                return new SequentialOutsideProcessor();
            default:
                throw new ApplicationException("Unkown outside mode");
        }
    }

    private void calculateInside(CykResult cykResult) {
        RulesTable rulesTable = cykResult.getRulesTable();
        ProbabilityArray probabilityArray = cykResult.getProbabilityArray();
        CykNavigator.forEach(rulesTable.getLength(), (i, j) ->
                rulesTable.getCellRules(i, j).forEach(cellRule ->
                        updateInside(probabilityArray, i, j, (PureCellRule) cellRule)
                )
        );
    }

    private void updateInside(ProbabilityArray probabilityArray, Integer i, Integer j, PureCellRule cellRule) {
        cellRule.setInside(probabilityArray.get(i, j, cellRule.getRule().getLeft()).getProbability());
//        LOGGER.debug("Inside of {} set to {}", cellRule.getRule(), cellRule.getInside());
    }
}
