package stopCondition;

import evaluation.ConfusionMatrix;
import evaluation.EvaluationService;

public class ValueStopCondition implements StopCondition {

    private final double expectedSpecificity;

    private final double expectedSensitivity;

    private final EvaluationService evaluationService;

    public ValueStopCondition(double expectedSpecificity, double expectedSensitivity, EvaluationService evaluationService) {
        this.expectedSpecificity = expectedSpecificity;
        this.expectedSensitivity = expectedSensitivity;
        this.evaluationService = evaluationService;
    }

    @Override
    public boolean shouldStop() {
        ConfusionMatrix confusionMatrix = evaluationService.getLastResult().getEvaluation();
        if (confusionMatrix != null) {
            return confusionMatrix.getSensitivity() >= expectedSensitivity && confusionMatrix.getSpecificity() >= expectedSpecificity;
        }
        return false;
    }
}
