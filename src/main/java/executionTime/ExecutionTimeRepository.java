package executionTime;

import java.util.*;

public class ExecutionTimeRepository {

    private Map<EtMarkersChain, List<Long>> executionTimesByMethods = new HashMap<>();

    private static ExecutionTimeRepository instance;

    private ExecutionTimeRepository() {
    }

    public static ExecutionTimeRepository getInstance() {
        if (instance == null)
            instance = new ExecutionTimeRepository();
        return instance;
    }

    public void insert(EtMarkersChain markers, Long executionTime){
        List<Long> executionTimes = executionTimesByMethods.computeIfAbsent(markers, k -> new ArrayList<>());
        executionTimes.add(executionTime);
    }

    public Map<EtMarkersChain, List<Long>> getResult(){
        return Collections.unmodifiableMap(executionTimesByMethods);
    }

}
