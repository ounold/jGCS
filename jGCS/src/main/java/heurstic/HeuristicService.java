package heurstic;

import application.ApplicationException;
import configuration.ConfigurationService;
import grammar.Grammar;
import heurstic.ga.GeneticAlgorithm;
import heurstic.splitAndMerge.SplitAndMerge;


public class HeuristicService {

    private static final String HEURISTIC_ALGORITHM = "heuristic.algorithm";

    private static HeuristicService instance;

    private final HeuristicAlgorithmType algorithmType;
    private final Heuristic algorithm;

    public HeuristicService() {
        algorithmType =  ConfigurationService.getConfiguration().getEnum(HeuristicAlgorithmType::valueOf, HEURISTIC_ALGORITHM);
        algorithm = initAlgorithm();
    }

    public static HeuristicService getInstance() {
        if (instance == null) {
            instance = new HeuristicService();
        }
        return instance;
    }

    public void run(Grammar grammar, int execution) {
        algorithm.process(grammar, execution);
    }

    public HeuristicAlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    private Heuristic initAlgorithm() {
        switch (algorithmType) {
            case GA:
                return new GeneticAlgorithm();
            case SPLIT_MERGE:
                return new SplitAndMerge();
            default:
                throw new ApplicationException("Heuristic algorithm unknown");
        }
    }

}
