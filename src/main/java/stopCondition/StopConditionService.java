package stopCondition;

import configuration.Configuration;
import configuration.ConfigurationService;
import evaluation.EvaluationService;
import evaluation.MaximizationTarget;
import io.ui.UiService;

public class StopConditionService {

    private static final String EXPECTED_SENSITIVITY = "sc.expectedSensitvity";
    private static final String EXPECTED_SPECIFICITY = "sc.expectedSpecificity";

    private static final String MAXIMIZATION_TARGET = "ev.maximizationTarget";
    private static final String EXPECTED_MT_DECREASE = "sc.expectedMtDecrease";
    private static final String MT_DECREASE_IN_STEPS = "sc.mtDecreaseInSteps";

    private static final String NUM_OF_ITERATIONS = "sc.iterations";

    private Configuration configuration = ConfigurationService.getConfiguration();

    private EvaluationService evaluationService = EvaluationService.getInstance();
    private StopConditionRepository stopConditionRepository = StopConditionRepository.getInstance();
    private UiService uiService = UiService.getInstance();

    private static StopConditionService instance;

    private StopConditionService() {
    }

    public void initializeStopConditions() {
        Integer numberOfIterations = configuration.getInteger(NUM_OF_ITERATIONS);

        if (numberOfIterations != null) {
            stopConditionRepository.insert(new IterationStopCondition(numberOfIterations, evaluationService));
            uiService.info("Maximum number of algorithm iterations: ", numberOfIterations);
        }

        Double expectedSensitivity = configuration.getDouble(EXPECTED_SENSITIVITY);
        Double expectedSpecificity = configuration.getDouble(EXPECTED_SPECIFICITY);

        if (expectedSensitivity != null && expectedSpecificity != null) {
            stopConditionRepository.insert(new ValueStopCondition(expectedSpecificity, expectedSensitivity, evaluationService));
            uiService.info("Stop condition: Sensitivity >= %f and Specificity >= %f initialized successfully", expectedSensitivity, expectedSpecificity);
        }

        MaximizationTarget maximizationTarget = configuration.getEnum(MaximizationTarget::valueOf, MAXIMIZATION_TARGET);
        Double expectedMtDecrease = configuration.getDouble(EXPECTED_MT_DECREASE);
        Integer mtDecreaseInSteps = configuration.getInteger(MT_DECREASE_IN_STEPS);

        if (maximizationTarget != null && expectedMtDecrease != null && mtDecreaseInSteps != null) {
            stopConditionRepository.insert(new DiffStopCondition(maximizationTarget, expectedMtDecrease, mtDecreaseInSteps, evaluationService));
            uiService.info("Stop condition: Decrease of %s >= %f in %d steps initialized successfully", maximizationTarget, expectedMtDecrease, mtDecreaseInSteps);
        }

        if(stopConditionRepository.getAll().isEmpty()){
            uiService.info("No stop condition found");
        }

    }

    public static StopConditionService getInstance() {
        if (instance == null)
            instance = new StopConditionService();
        return instance;
    }

    public boolean shouldStop() {
        return stopConditionRepository.getAll().stream().anyMatch(StopCondition::shouldStop);
    }

    public void clearStopConditions() {
        stopConditionRepository.clear();
    }

}
