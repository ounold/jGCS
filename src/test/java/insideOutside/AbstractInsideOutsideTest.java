package insideOutside;

import common.AbstractServiceTest;
import grammar.Rule;
import org.junit.jupiter.api.Assertions;
import rulesTable.RulesTable;

public abstract class AbstractInsideOutsideTest extends AbstractServiceTest {

    protected static final double DELTA = .000001;

    protected double extractOutside(RulesTable rulesTable, int i, int j, Rule rule) {
        return rulesTable.getCellRule(i, j, rule).getOutside();
    }

    protected double extractInside(RulesTable rulesTable, int i, int j, Rule rule) {
        return rulesTable.getCellRule(i, j, rule).getInside();
    }

    protected void assertInsideOutsideValues(Rule rule, double positiveUsages, double negativeUsages, int usageProbability){
        Assertions.assertEquals(positiveUsages, rule.getPositiveSumInsideOutsideUsages(), DELTA);
        Assertions.assertEquals(negativeUsages, rule.getNegativeSumInsideOutsideUsages(), DELTA);
        Assertions.assertEquals(usageProbability, rule.getCountInsideOutsideUsageProbability());
    }

    protected void assertRulesCounts(Rule rule, double count, double positiveCount, double negativeCount){
        Assertions.assertEquals(positiveCount, rule.getPositiveCount(), DELTA);
        Assertions.assertEquals(negativeCount, rule.getNegativeCount(), DELTA);
    }
}
