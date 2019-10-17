package insideOutside.outside;

import cyk.CykNavigator;
import cyk.CykResult;
import insideOutside.IoSequence;
import rulesTable.CellRule;
import rulesTable.RulesTable;

public class SequentialOutsideProcessor extends OutsideProcessor {

    @Override
    public void calculateOutside(IoSequence sequence, CykResult cykResult) {
        RulesTable rulesTable = cykResult.getRulesTable();
        for (CellRule cellRule : rulesTable.getStartCellRules()) {
            cellRule.setOutside(1);
        }
        CykNavigator.forEachReversed(rulesTable.getLength(), (i, j) -> {
            updateCell(rulesTable, i, j);
        });
    }

}
