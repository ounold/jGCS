package stopCondition;


import evaluation.ConfusionMatrix;
import evaluation.Evaluation;
import evaluation.EvaluationService;
import evaluation.MaximizationTarget;

import java.util.List;
import java.util.function.Function;

public class DiffStopCondition implements StopCondition {

    private final Function<ConfusionMatrix, Double> extractor;

    private final double expectedDecrease;

    private final int inSteps;

    private final EvaluationService evaluationService;

    public DiffStopCondition(MaximizationTarget maximizationTarget, double expectedDecrease, int inSteps, EvaluationService evaluationService) {
        this.extractor = maximizationTarget.getExtractor();
        this.expectedDecrease = expectedDecrease;
        this.inSteps = inSteps;
        this.evaluationService = evaluationService;
    }

    @Override
    public boolean shouldStop() {
        List<Evaluation> results = evaluationService.getLastResultsDesc(inSteps + 1);
        if(results.size() < inSteps + 1)
            return false;
        return extractor.apply(results.get(0).getEvaluation()) - extractor.apply(results.get(inSteps).getEvaluation()) < -expectedDecrease;
    }
}
