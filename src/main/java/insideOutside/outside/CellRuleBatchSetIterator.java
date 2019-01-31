package insideOutside.outside;

import cyk.CykNavigator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rulesTable.CellRule;
import rulesTable.RulesTable;

import java.util.*;
import java.util.stream.Collectors;

public class CellRuleBatchSetIterator{

    private static final int END_OF_SET = -1;

    private static final Logger LOGGER = LogManager.getLogger(CellRuleBatchSetIterator.class);

    private final Map<Integer, List<CellRule>> cellRuleBatches = new HashMap<>();

    private final Map<Integer, Iterator<CellRule>> cellRuleBatchIterators;

    private volatile int batchIndex = END_OF_SET;

    public CellRuleBatchSetIterator(RulesTable rulesTable){
        CykNavigator.forEachReversed(rulesTable.getLength(), (i, j) -> {
            List<CellRule> batch = cellRuleBatches.computeIfAbsent(i, n -> new ArrayList<>());
            batch.addAll(rulesTable.getCellRules(i, j));
            if(i > batchIndex && !batch.isEmpty())
                batchIndex = i;
        });
        cellRuleBatchIterators = cellRuleBatches.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue()).iterator()));
    }

    public synchronized void markProceeded(CellRule cellRule){
        List<CellRule> currentBatch = cellRuleBatches.get(batchIndex);
        currentBatch.remove(cellRule);
        LOGGER.debug("Marking CR {} from {}", cellRule.getRule().getDefinition(), cellRule.getRootCellCoordinates());
        if(currentBatch.isEmpty())
            do {
                batchIndex--;
                LOGGER.debug("Opening batch: {}", batchIndex);
            } while (batchIndex > END_OF_SET && cellRuleBatches.get(batchIndex).isEmpty());
    }

    public synchronized CellRule next() {
        Iterator<CellRule> currentIterator = cellRuleBatchIterators.get(batchIndex);
        if(batchIndex != END_OF_SET && currentIterator.hasNext()){
            CellRule result = currentIterator.next();
            LOGGER.debug("Getting CR {} from {}", result.getRule().getDefinition(), result.getRootCellCoordinates());
            return result;
        }
        LOGGER.debug("Waiting");
        return null;
    }

    public synchronized boolean hasNext() {
        return batchIndex > END_OF_SET;
    }

}
