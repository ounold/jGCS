package rulesTable;

import grammar.Rule;
import lombok.Setter;

@Deprecated
public class PureCellRule extends CellRule {

    @Setter
    private double inside;

    public PureCellRule(Rule rule, Coordinate self) {
        super(rule, self);
    }

    public PureCellRule(Rule rule, Coordinate c1, Coordinate c2, Coordinate self) {
        super(rule, c1, c2, self);
    }

    @Override
    public double getInside() {
        return inside;
    }
}
