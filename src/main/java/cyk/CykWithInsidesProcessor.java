package cyk;

import grammar.Rule;
import probabilityArray.ProbabilityCell;
import rulesTable.CellRule;
import rulesTable.CellRuleWithInside;
import rulesTable.Coordinate;

public class CykWithInsidesProcessor extends CykProcessor {

    public CykWithInsidesProcessor(boolean enableCovering) {
        super(enableCovering);
    }

    @Override
    protected CellRule createCellRule(int i, int j, int k, Rule rule, ProbabilityCell probabilityCell) {
        return new CellRuleWithInside(rule, new Coordinate(k, j), new Coordinate(i - k - 1, j + k + 1), probabilityCell, new Coordinate(i, j));
    }

    @Override
    protected CellRule createCellRule(Rule rule, ProbabilityCell probabilityCell, int i) {
        return new CellRuleWithInside(rule, probabilityCell, new Coordinate(0, i));
    }

}
