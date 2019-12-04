package evaluation;

import common.AbstractServiceTest;
import configuration.ConfigurationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

class EvaluationServiceTest extends AbstractServiceTest {

    private static final String MAXIMIZATION_TARGET = "ev.maximizationTarget";

    EvaluationService evaluationService = EvaluationService.getInstance();

    @BeforeEach
    public void setUp() {
        initEvaluationRepository();
    }

    @AfterEach
    public void clear() {
        evaluationService.clearEvaluation();
    }

    @Test
    public void shouldGetBestResultForF1() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "F1");

        // when
        Evaluation evaluation = evaluationService.getBestResult(null);

        // then
        Assertions.assertEquals(evaluationService.getResult().get(2), evaluation);
    }

    @Test
    public void shouldGetBestResultForSpecificity() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "SPECIFICITY");

        // when
        Evaluation evaluation = evaluationService.getBestResult(null);

        // then
        Assertions.assertEquals(evaluationService.getResult().get(4), evaluation);
    }

    @Test
    public void shouldGetBestResultForSensitivity() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "SENSITIVITY");

        // when
        Evaluation evaluation = evaluationService.getBestResult(null);

        // then
        Assertions.assertEquals(evaluationService.getResult().get(5), evaluation);
    }

    @Test
    public void shouldGetBestResultForPrecision() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "PRECISION");

        // when
        Evaluation evaluation = evaluationService.getBestResult(null);

        // then
        Assertions.assertEquals(evaluationService.getResult().get(2), evaluation);
    }

    @Test
    public void shouldGetBestResultForBetterBestTillNow() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "F1");
        Evaluation bestTillNow = new Evaluation(
                "",
                1,
                new ConfusionMatrix(9, 1, 1, 1)
        );

        // when
        Evaluation evaluation = evaluationService.getBestResult(bestTillNow);

        // then
        Assertions.assertEquals(bestTillNow, evaluation);
    }

    @Test
    public void shouldGetBestResultForWorseBestTillNow() {
        // given
        ConfigurationService.getInstance().overrideProperty(MAXIMIZATION_TARGET, "F1");
        Evaluation bestTillNow = new Evaluation(
                "",
                1,
                new ConfusionMatrix(1, 1, 1, 1)
        );

        // when
        Evaluation evaluation = evaluationService.getBestResult(bestTillNow);

        // then
        Assertions.assertEquals(evaluationService.getResult().get(2), evaluation);
    }

    private void initEvaluationRepository() {
        evaluationService.saveEvaluation(1, createEvaluationSupplier(1, 1, 1, 1));
        evaluationService.saveEvaluation(2, createEvaluationSupplier(2, 1, 1, 1));
        evaluationService.saveEvaluation(3, createEvaluationSupplier(1, 2, 1, 1));
        evaluationService.saveEvaluation(4, createEvaluationSupplier(1, 1, 2, 1));
        evaluationService.saveEvaluation(5, createEvaluationSupplier(5, 4, 1, 2));
    }

    private Supplier<Evaluation> createEvaluationSupplier(int tp, int fp, int tn, int fn) {
        return () -> new Evaluation(
                "",
                1,
                new ConfusionMatrix(tp, fp, tn, fn)
        );
    }

}