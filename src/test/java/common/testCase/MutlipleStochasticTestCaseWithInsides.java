package common.testCase;

import cyk.CykResult;
import grammar.Rule;
import rulesTable.RulesTable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MutlipleStochasticTestCaseWithInsides extends MutlipleStochasticTestCase {

    @Override
    public void assertResult(CykResult cykResult) {
        super.assertResult(cykResult);
        assertInsides(cykResult);
    }

    private void assertInsides(CykResult cykResult) {
        RulesTable rulesTable = cykResult.getRulesTable();
        assertEquals(extractInside(rulesTable, 0, 0, A_a), .2, DELTA);
        assertEquals(extractInside(rulesTable, 0, 1, B_b), .6, DELTA);
        assertEquals(extractInside(rulesTable, 0, 2, C_c), .4, DELTA);
        assertEquals(extractInside(rulesTable, 1, 0, S_AB), .012, DELTA);
        assertEquals(extractInside(rulesTable, 1, 1, S_BC), .072, DELTA);
        assertEquals(extractInside(rulesTable, 2, 0, S_AS), .01248, DELTA);
        assertEquals(extractInside(rulesTable, 2, 0, S_SC), .01248, DELTA);
    }

    private double extractInside(RulesTable rulesTable, int i, int j, Rule rule) {
        return rulesTable.getCellRule(i, j, rule).getInside();
    }


}
