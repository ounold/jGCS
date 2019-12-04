package stopCondition;

import evaluation.ConfusionMatrix;
import evaluation.EvaluationService;

public class ValueStopCondition implements StopCondition {

//    private final double expectedSpecificity;
//
//    private final double expectedSensitivity;

    private final double expectedF1;

    private final EvaluationService evaluationService;

    public ValueStopCondition(double expectedF1, EvaluationService evaluationService) {
//        this.expectedSpecificity = expectedSpecificity;
//        this.expectedSensitivity = expectedSensitivity;
        this.expectedF1 = expectedF1;
        this.evaluationService = evaluationService;
    }

    @Override
    public boolean shouldStop() {
        if (evaluationService.getLastResult() != null) {
            ConfusionMatrix confusionMatrix = evaluationService.getLastResult().getEvaluation();
            if (confusionMatrix != null) {
                return confusionMatrix.getF1() >= expectedF1;
            }
            return false;
        }
        return false;
    }
}
