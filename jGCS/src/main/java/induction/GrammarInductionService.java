package induction;

import application.Application;
import correction.GrammarCorrectionService;
import cyk.CykResult;
import cyk.CykService;
import dataset.Dataset;
import evaluation.EvaluationService;
import executionTime.EtMarkers;
import executionTime.EtMarkersChain;
import executionTime.ExecutionTimeService;
import fitness.FitnessService;
import grammar.Grammar;
import heurstic.HeuristicService;
import insideOutside.InsideOutsideService;
import insideOutside.IoDataset;
import insideOutside.neighbourhood.NeighbourhoodService;
import io.ui.UiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stopCondition.StopConditionService;

public class GrammarInductionService {

    public static final EtMarkersChain ETMC_NORMALIZATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_NORMALIZATION);
    public static final EtMarkersChain ETMC_REMOVING_RULES = Application.ETMC_INDUCTION.get(EtMarkers.ET_REMOVING_RULES);
    public static final EtMarkersChain ETMC_CORRECTION = Application.ETMC_INDUCTION.get(EtMarkers.ET_CORRECTION);
    public static final EtMarkersChain ETMC_EVALUATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_EVALUATION);
    public static final EtMarkersChain ETMC_ITERATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_ITERATION);
    public static final EtMarkersChain ETM_HEURISTIC = Application.ETMC_INDUCTION.get(EtMarkers.ET_HEURISTIC);
    public static final EtMarkersChain ETMC_SEQUENCE = ETMC_ITERATION.get(EtMarkers.ET_SEQUENCE);
    public static final EtMarkersChain ETMC_CYK = ETMC_SEQUENCE.get(EtMarkers.ET_CYK);
    public static final EtMarkersChain ETMC_IO_COUNTS = ETMC_SEQUENCE.get(EtMarkers.ET_IO_COUNTS);
    public static final EtMarkersChain ETMC_IO_PROBABILITIES = ETMC_ITERATION.get(EtMarkers.ET_IO_PROBABILITIES);

    private static final Logger LOGGER = LogManager.getLogger(GrammarInductionService.class);

    private InsideOutsideService insideOutsideService = InsideOutsideService.getInstance();
    private NeighbourhoodService neighbourhoodService = NeighbourhoodService.getInstance();
    private GrammarCorrectionService correctionService = GrammarCorrectionService.getInstance();
    private CykService cykService = CykService.getInstance();
    private UiService uiService = UiService.getInstance();
    private EvaluationService evaluationService = EvaluationService.getInstance();
    private ExecutionTimeService executionTimeService = ExecutionTimeService.getInstance();
    private StopConditionService stopConditionService = StopConditionService.getInstance();
    private HeuristicService heuristicService = HeuristicService.getInstance();
    private FitnessService fitnessService = FitnessService.getInstance();

    private static GrammarInductionService instance;

    private GrammarInductionService() {
    }

    public static GrammarInductionService getInstance() {
        if (instance == null)
            instance = new GrammarInductionService();
        return instance;
    }

    public void run(Grammar grammar, Dataset dataset, Dataset testDataset, int trainIterations, boolean enableCovering) {
        IoDataset ioDataset = neighbourhoodService.buildNeighbourhoods(dataset);
        int iteration = 0;
        while (!stopConditionService.shouldStop()) {
//            grammar.getNonTerminalRules().removeIf(rule -> rule.getFitness() <= 0);
//            long startProgramTime = System.currentTimeMillis();

            iteration++;
            final int iter = iteration;

            uiService.info("\nIteration: %d", iteration);
//            System.out.println("\nRules on start of iteration");
//            grammar.getRules()
//                    .forEach(System.out::println);
            if (iteration > 1) {
                executionTimeService.saveExecutionTime(ETM_HEURISTIC, () -> heuristicService.run(grammar, iter));
            }
            if (enableCovering && iteration == 1) {
                System.out.println("Covering");
                ioDataset.getSequences().forEach(ioSequence -> executionTimeService.saveExecutionTime(ETMC_SEQUENCE, () -> {
                    executionTimeService.saveExecutionTime(ETMC_CYK,
                            () -> cykService.runCyk(ioSequence.getSequence(), grammar, ioSequence.getSequence().isPositive()));
                }));
            }
            correctionService.removeDuplicatedRules(grammar);
            fitnessService.resetRulesUsagesDebtsProfits(grammar);
            for (int i = 0; i < trainIterations; i++) {
                final int trainIter = i;
                executionTimeService.saveExecutionTime(ETMC_ITERATION, () -> {
//                    final long[] sum = {0};
                    uiService.state("\nTrain iteration: %d \n", trainIter + 1);
                    insideOutsideService.resetInsideOutsideValuesAndCounts(grammar);
                    ioDataset.getSequences().forEach(ioSequence -> executionTimeService.saveExecutionTime(ETMC_SEQUENCE, () -> {
                        insideOutsideService.resetInsideOutsideValues(grammar);
                        CykResult cykResult = executionTimeService.saveExecutionTime(ETMC_CYK, () -> cykService.runCyk(ioSequence.getSequence(), grammar, false));
                        fitnessService.countRulesUsage(ioSequence.getSequence(), cykResult, grammar);
//                        fitnessService.fillDebtsProfits(ioSequence.getSequence(), ioSequence.isPositive(), cykResult);
                        executionTimeService.saveExecutionTime(ETMC_IO_COUNTS, () -> insideOutsideService.updateRulesCounts(grammar, cykResult, ioSequence));
                    }));
//                    System.out.println("FITNESS TIME: " + sum[0] / 1000);
                    correctionService.removeZeroProbabilitiesRules(grammar);
//                    evaluationService.evaluateDataset(dataset, grammar);
//                    evaluationService.evaluateDataset(testDataset, grammar);
                    executionTimeService.saveExecutionTime(ETMC_IO_PROBABILITIES, () -> insideOutsideService.updateRulesProbabilities(grammar));
                });
            }
//            executionTimeService.saveExecutionTime(ETMC_REMOVING_RULES, () -> correctionService.removeZeroProbabilitiesRules(grammar));
            fitnessService.countRulesFitness(grammar);


//            fitnessService.countClassicRulesFitness(grammar);
            evaluationService.saveEvaluation(iter, () -> executionTimeService.saveExecutionTime(ETMC_EVALUATION,
                    () -> evaluationService.evaluateDataset(testDataset, grammar)));
//            long endProgramTime = System.currentTimeMillis();
//            long evolutionDifferenceTime = (endProgramTime - startProgramTime) / 1000;
//            System.out.println("EVOLUTION STEP TIME: " + evolutionDifferenceTime + "\n");
        }
        executionTimeService.saveExecutionTime(ETMC_CORRECTION, () -> correctionService.correctGrammar(grammar));
        uiService.closeState();
    }

}
