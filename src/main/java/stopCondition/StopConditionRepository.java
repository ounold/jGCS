package stopCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StopConditionRepository {

    private List<StopCondition> stopConditions = new ArrayList<>();

    private static StopConditionRepository instance;

    private StopConditionRepository() {
    }

    public static StopConditionRepository getInstance() {
        if (instance == null)
            instance = new StopConditionRepository();
        return instance;
    }

    public void insert(StopCondition stopCondition) {
        stopConditions.add(stopCondition);
    }

    public List<StopCondition> getAll() {
        return Collections.unmodifiableList(stopConditions);
    }

    public void clear() {
        stopConditions.clear();
    }

}
