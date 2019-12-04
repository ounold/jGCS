package stopCondition;

import evaluation.EvaluationService;

public class IterationStopCondition implements StopCondition {

    private Integer numberOfIterations;

    private final EvaluationService evaluationService;


    public IterationStopCondition(Integer numberOfIterations, EvaluationService evaluationService) {
        this.numberOfIterations = numberOfIterations;
        this.evaluationService = evaluationService;
    }

    @Override
    public boolean shouldStop() {
        Integer iteration = evaluationService.getResult().keySet().stream()
                .sorted()
                .mapToInt(i -> i)
                .max()
                .orElse(0);
        return iteration.equals(numberOfIterations);
    }
}
