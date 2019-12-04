package evaluation;

import application.ApplicationException;

import java.util.*;
import java.util.stream.Collectors;

public class EvaluationRepository {

    private final Map<Integer, Evaluation> result = new HashMap<>();

    private static EvaluationRepository instance;

    private EvaluationRepository() {
    }

    public static EvaluationRepository getInstance() {
        if (instance == null)
            instance = new EvaluationRepository();
        return instance;
    }

    public void insert(int iteration, Evaluation evaluation) {
//        if (result.containsKey(iteration))
//            throw new ApplicationException(String.format("Matrix with iteration number %d already exists", iteration));
        result.put(iteration, evaluation);
    }

    public Map<Integer, Evaluation> getResult() {
        return Collections.unmodifiableMap(result);
    }

    public Evaluation getLastResult() {
        return result.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Integer, Evaluation>>comparingInt(Map.Entry::getKey).reversed())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public List<Evaluation> getLastResultsDesc(int limit) {
        return result.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Integer, Evaluation>>comparingInt(Map.Entry::getKey).reversed())
                .map(Map.Entry::getValue)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void clear(){
        result.clear();
    }

}
