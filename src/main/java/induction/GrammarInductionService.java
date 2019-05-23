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
import grammar.Grammar;
import insideOutside.InsideOutsideService;
import insideOutside.IoDataset;
import insideOutside.neighbourhood.NeighbourhoodService;
import io.ui.UiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stopCondition.StopConditionService;

public class GrammarInductionService {

    public static final EtMarkersChain ETMC_NORMALIZATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_NORMALIZATION);
    public static final EtMarkersChain ETMC_CORRECTION = Application.ETMC_INDUCTION.get(EtMarkers.ET_CORRECTION);
    public static final EtMarkersChain ETMC_EVALUATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_EVALUATION);
    public static final EtMarkersChain ETMC_ITERATION = Application.ETMC_INDUCTION.get(EtMarkers.ET_ITERATION);
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

    private static GrammarInductionService instance;

    private GrammarInductionService() {
    }

    public static GrammarInductionService getInstance() {
        if (instance == null)
            instance = new GrammarInductionService();
        return instance;
    }

    public void run(Grammar grammar, Dataset dataset, Dataset testDataset, int iterations, boolean enableCovering) {
        executionTimeService.saveExecutionTime(ETMC_NORMALIZATION, () -> correctionService.normalizeRules(grammar));
        IoDataset ioDataset = neighbourhoodService.buildNeighbourhoods(dataset);
        int i = 0;
        for (; ; i++) {
            int iteration = i;
            if (iteration == iterations) {
                evaluationService.saveEvaluation(i, () -> executionTimeService.saveExecutionTime(ETMC_EVALUATION,
                        () -> evaluationService.evaluateDataset(testDataset, grammar)));
                break;
            }
            evaluationService.saveEvaluationConditionally(i, () -> executionTimeService.saveExecutionTime(ETMC_EVALUATION,
                    () -> evaluationService.evaluateDataset(testDataset, grammar)));
            if (stopConditionService.shouldStop())
                break;
            executionTimeService.saveExecutionTime(ETMC_ITERATION, () -> {
                uiService.state("Iteration: %d", iteration + 1);
                insideOutsideService.resetInsideOutsideValuesAndCounts(grammar);
                ioDataset.getSequences().forEach(ioSequence -> {
                    executionTimeService.saveExecutionTime(ETMC_SEQUENCE, () -> {
                        insideOutsideService.resetInsideOutsideValues(grammar);
                        CykResult cykResult = executionTimeService.saveExecutionTime(ETMC_CYK,
                                () -> cykService.runCyk(ioSequence.getSequence(), grammar, enableCovering));
                        executionTimeService.saveExecutionTime(ETMC_IO_COUNTS,
                                () -> insideOutsideService.updateRulesCounts(grammar, cykResult, ioSequence));
                    });
                });
                executionTimeService.saveExecutionTime(ETMC_CORRECTION, () -> correctionService.correctGrammar(grammar));
                executionTimeService.saveExecutionTime(ETMC_IO_PROBABILITIES,
                        () -> insideOutsideService.updateRulesProbabilities(grammar));
            });
        }
        uiService.closeState();
    }

}
