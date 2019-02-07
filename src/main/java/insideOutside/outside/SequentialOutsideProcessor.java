package insideOutside.outside;

import cyk.CykNavigator;
import cyk.CykResult;
import insideOutside.IoSequence;
import rulesTable.RulesTable;

public class SequentialOutsideProcessor extends OutsideProcessor {

    @Override
    public void calculateOutside(IoSequence sequence, CykResult cykResult) {
        RulesTable rulesTable = cykResult.getRulesTable();
        rulesTable.getStartCellRulesStream().forEach(cellRule -> cellRule.setOutside(1));
        CykNavigator.forEachReversed(rulesTable.getLength(), (i, j) -> {
            updateCell(rulesTable, i, j);
        });
    }

}
