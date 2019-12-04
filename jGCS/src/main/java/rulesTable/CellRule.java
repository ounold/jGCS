package rulesTable;

import grammar.Rule;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

@Data
public abstract class CellRule {

    private static final Logger LOGGER = LogManager.getLogger(CellRule.class);

    private Rule rule;

    private Coordinate cell1Coordinates;

    private Coordinate cell2Coordinates;

    private Coordinate rootCellCoordinates;

    private double outside;

    private AtomicBoolean proceeded = new AtomicBoolean(false);

    private boolean calculated;

    private double tmpVal;

    public CellRule(Rule rule, Coordinate self) {
        this.rule = rule;
        this.rootCellCoordinates = self;
    }

    public CellRule(Rule rule, Coordinate c1, Coordinate c2, Coordinate self) {
        this.rule = rule;
        this.cell1Coordinates = c1;
        this.cell2Coordinates = c2;
        this.rootCellCoordinates = self;
    }

    public boolean isStart() {
        return getRule().isStart();
    }

    public abstract double getInside();

    public boolean isProceeded(){
        return proceeded.get();
    }

    public synchronized void addOutside(double value){
//        LOGGER.error("Outside of {}: {} updated. {} + {} = {}", rootCellCoordinates, rule.getDefinition(), outside, value, value + outside);
        this.outside += value;
    }
}
