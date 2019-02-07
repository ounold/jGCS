package insideOutside.outside;

import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykResult;
import insideOutside.IoSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rulesTable.CellRule;
import rulesTable.RulesTable;
import rulesTable.TableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Deprecated
public class CellRuleConcurrentOutsideProcessor extends OutsideProcessor {

    private static final Logger LOGGER = LogManager.getLogger(CellRuleConcurrentOutsideProcessor.class);

    private static final String NUM_OF_THREADS = "ce.numOfThreads";

    private static ExecutorService executors;

    private Configuration configuration = ConfigurationService.getConfiguration();

    private CellRuleBatchSetIterator jobIterator;

    private RulesTable rulesTable;

    private boolean calculateOutside() throws InterruptedException {
        int sleepTime = 1;
        while (jobIterator.hasNext()){
            CellRule cellRule = jobIterator.next();
            if(cellRule == null) {
                Thread.sleep(sleepTime++);
                continue;
            }
            sleepTime = 1;
            updateChildRules(rulesTable, cellRule);
            jobIterator.markProceeded(cellRule);
        }

        return true;
    }

    private void initLastRow() {
        TableCell startTableCell = rulesTable.getStartTableCell();
        startTableCell.getCellRules().forEach(cellRule -> {
            if (cellRule.isStart())
                cellRule.setOutside(1);
//            LOGGER.error("Proceeded: Cell {}, Rule {}", cellRule.getRootCellCoordinates(), cellRule.getRule().getDefinition());
        });
    }

    public void calculateOutside(IoSequence sequence, CykResult cykResult) {
        Integer numOfThreads = configuration.getInteger(NUM_OF_THREADS);
        executors = Executors.newFixedThreadPool(numOfThreads);
        rulesTable = cykResult.getRulesTable();
        jobIterator = new CellRuleBatchSetIterator(rulesTable);
        initLastRow();
        List<Callable<Object>> todo = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            todo.add(this::calculateOutside);
        }
        try {
            List<Future<Object>> status = executors.invokeAll(todo);

            for (Future<Object> s : status) {
                s.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
        executors.shutdown();
    }

}
