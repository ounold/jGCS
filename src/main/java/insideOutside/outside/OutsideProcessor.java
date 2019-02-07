package insideOutside.outside;

import cyk.CykResult;
import executionTime.ExecutionTimeService;
import insideOutside.IoSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rulesTable.CellRule;
import rulesTable.RulesTable;

import java.util.List;

public abstract class OutsideProcessor {

    protected ExecutionTimeService executionTimeService = ExecutionTimeService.getInstance();

    private static final Logger LOGGER = LogManager.getLogger(OutsideProcessor.class);

    public abstract void calculateOutside(IoSequence sequence, CykResult cykResult);

    protected void updateCell(RulesTable rulesTable, int i, int j) {
//        LOGGER.error("Proceeding cell ({}, {})", i, j);
        for (CellRule parentRule : rulesTable.getCellRules(i, j)) {
            if (parentRule.isProceeded())
                return;
//                    LOGGER.error("Proceeding rule {} of cell ({}, {})", parentRule.getRule().getDefinition(), i, j);
            parentRule.getProceeded().compareAndSet(false, true);
            updateChildRules(rulesTable, parentRule);
        }
        rulesTable.get(i, j).getProceeded().compareAndSet(false, true);
//        LOGGER.error("Proceeding cell ({}, {}) ended", i, j);
    }

    protected void updateChildRules(RulesTable rulesTable, CellRule parentRule) {
        List<CellRule> children1 = rulesTable.findMatchingRules(parentRule.getCell1Coordinates(), parentRule.getRule().getRight1());
        List<CellRule> children2 = rulesTable.findMatchingRules(parentRule.getCell2Coordinates(), parentRule.getRule().getRight2());
        if (!children1.isEmpty() && !children2.isEmpty()) {
            children1.forEach(childRule ->
                    updateOutside(parentRule, childRule, children2)
            );
            children2.forEach(childRule ->
                    updateOutside(parentRule, childRule, children1)
            );
        }
    }

    protected void updateOutside(CellRule parentRule, CellRule childRule, List<CellRule> sisterRules) {
        childRule.addOutside(parentRule.getOutside() * parentRule.getRule().getProbability() * sisterRules.get(0).getInside());
    }

}
