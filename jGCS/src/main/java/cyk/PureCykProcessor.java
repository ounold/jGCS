package cyk;

import grammar.Rule;
import probabilityArray.ProbabilityCell;
import rulesTable.CellRule;
import rulesTable.Coordinate;
import rulesTable.PureCellRule;

@Deprecated
public class PureCykProcessor extends CykProcessor {

    public PureCykProcessor(boolean enableCovering) {
        super(enableCovering);
    }

    @Override
    protected CellRule createCellRule(int i, int j, int k, Rule rule, ProbabilityCell probabilityCell) {
        return new PureCellRule(rule, new Coordinate(k, j), new Coordinate(i - k - 1, j + k + 1), new Coordinate(i, j));
    }

    @Override
    protected CellRule createCellRule(Rule rule, ProbabilityCell probabilityCell, int i) {
        return new PureCellRule(rule, new Coordinate(0, i));
    }
}
