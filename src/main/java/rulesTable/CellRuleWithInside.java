package rulesTable;

import grammar.Rule;
import probabilityArray.ProbabilityCell;

public class CellRuleWithInside extends CellRule {

    private ProbabilityCell probabilityCell;

    public CellRuleWithInside(Rule rule, ProbabilityCell probabilityCell, Coordinate self) {
        super(rule, self);
        this.probabilityCell = probabilityCell;
    }

    public CellRuleWithInside(Rule rule, Coordinate coordinate, Coordinate coordinate1, ProbabilityCell probabilityCell, Coordinate self) {
        super(rule, coordinate, coordinate1, self);
        this.probabilityCell = probabilityCell;
    }

    @Override
    public double getInside() {
        return probabilityCell.getProbability();
    }
}
