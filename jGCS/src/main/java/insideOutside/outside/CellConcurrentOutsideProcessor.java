package insideOutside.outside;

import configuration.Configuration;
import configuration.ConfigurationService;
import cyk.CykNavigator;
import cyk.CykResult;
import insideOutside.IoSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rulesTable.Coordinate;
import rulesTable.RulesTable;
import rulesTable.TableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Deprecated
public class CellConcurrentOutsideProcessor extends OutsideProcessor {

    private static final Logger LOGGER = LogManager.getLogger(CellConcurrentOutsideProcessor.class);

    private static final String NUM_OF_THREADS = "ce.numOfThreads";

    private static ExecutorService executors;

    private Configuration configuration = ConfigurationService.getConfiguration();

    private ConcurrentLinkedQueue<Coordinate> jobs = new ConcurrentLinkedQueue<>();

    private RulesTable rulesTable;

    private void prepareJobs(int sentenceLength) {
        CykNavigator.forEachReversed(sentenceLength, (i, j) -> {
            jobs.add(new Coordinate(i, j));
        });
    }

    private boolean calculateOutside() {

        while (true) {
            Coordinate cell = jobs.poll();

            if (cell == null){
                return true;
            }

            waitUntilProceeded(cell.getX() + 1, cell.getY(), cell.getX() + 1, cell.getY() - 1);

            updateCell(rulesTable, cell);
        }

    }

    protected void updateCell(RulesTable rulesTable, Coordinate coordinate) {
        super.updateCell(rulesTable, coordinate.getX(), coordinate.getY());
    }

    private void initLastRow() {
        TableCell startTableCell = rulesTable.getStartTableCell();
        startTableCell.getCellRules().forEach(cellRule -> {
            if (cellRule.isStart())
                cellRule.setOutside(1);
//            LOGGER.error("Proceeded: Cell {}, Rule {}", cellRule.getRootCellCoordinates(), cellRule.getRule().getDefinition());
        });
    }

    private void waitUntilProceeded(int parentOneI, int parentOneJ, int parentTwoI, int parentTwoJ) {
        while (!rulesTable.getNullable(parentOneI, parentOneJ).map(TableCell::isProceeded).orElse(true)
                || !rulesTable.getNullable(parentTwoI, parentTwoJ).map(TableCell::isProceeded).orElse(true)) ;
    }

    public void calculateOutside(IoSequence sequence, CykResult cykResult) {
        Integer numOfThreads = configuration.getInteger(NUM_OF_THREADS);
        executors = Executors.newFixedThreadPool(numOfThreads);
        rulesTable = cykResult.getRulesTable();
        initLastRow();
        prepareJobs(sequence.length());
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
