package stopCondition;

import common.AbstractServiceTest;
import configuration.ConfigurationService;
import evaluation.ConfusionMatrix;
import evaluation.Evaluation;
import evaluation.EvaluationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StopConditionServiceTest extends AbstractServiceTest {

    EvaluationService evaluationService = EvaluationService.getInstance();
    ConfigurationService configurationService = ConfigurationService.getInstance();
    StopConditionService stopConditionService = StopConditionService.getInstance();

    private static final String EXPECTED_SENSITIVITY = "sc.expectedSensitvity";
    private static final String EXPECTED_SPECIFICITY = "sc.expectedSpecificity";

    private static final String EXPECTED_MT_DECREASE = "sc.expectedMtDecrease";
    private static final String MT_DECREASE_IN_STEPS = "sc.mtDecreaseInSteps";

    @AfterEach
    public void clear() {
        stopConditionService.clearStopConditions();
        evaluationService.clearEvaluation();
        configurationService.resetProperties();
    }

    @Test
    public void shouldStopForValue() {
        // when
        initValueStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(10, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));
        evaluationService.saveEvaluation(12, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));

        // when
        assertTrue(stopConditionService.shouldStop());
    }

    @Test
    public void shouldNotStopForValue() {
        // when
        initValueStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(15, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(20, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));

        // when
        assertFalse(stopConditionService.shouldStop());

    }

    @Test
    public void shouldStopForDiff() {
        // when
        initDiffStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(10, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(21, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(33, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));

        // when
        assertTrue(stopConditionService.shouldStop());
    }

    @Test
    public void shouldNotStopForDiffIfTooSmall() {
        // when
        initDiffStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(11, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(22, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));
        evaluationService.saveEvaluation(33, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));

        // when
        assertFalse(stopConditionService.shouldStop());
    }

    @Test
    public void shouldNotStopForDiffIfTooFewElements() {
        // when
        initDiffStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(2, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(3, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));

        // when
        assertFalse(stopConditionService.shouldStop());
    }

    @Test
    public void shouldStopForAny() {
        // when
        initValueStopConditionParams();
        initDiffStopConditionParams();
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(3, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(4, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(5, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));

        // when
        assertTrue(stopConditionService.shouldStop());
    }

    @Test
    public void shouldNotStopForNone() {
        // when
        stopConditionService.initializeStopConditions();

        evaluationService.saveEvaluation(3, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(6, () -> new Evaluation("", 1, new ConfusionMatrix(1, 0, 1, 0)));
        evaluationService.saveEvaluation(9, () -> new Evaluation("", 1, new ConfusionMatrix(1, 1, 1, 1)));

        // when
        assertFalse(stopConditionService.shouldStop());
    }

    private void initValueStopConditionParams() {
        configurationService.overrideProperty(EXPECTED_SENSITIVITY, "0.8");
        configurationService.overrideProperty(EXPECTED_SPECIFICITY, "0.8");
    }

    private void initDiffStopConditionParams() {
        configurationService.overrideProperty(EXPECTED_MT_DECREASE, "0.2");
        configurationService.overrideProperty(MT_DECREASE_IN_STEPS, "2");
    }

}