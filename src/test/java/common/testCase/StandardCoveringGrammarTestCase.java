package common.testCase;

import common.grammar.SimpleTestGrammar;
import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import rulesTable.RulesTable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardCoveringGrammarTestCase extends GrammarTestCase {
    private static final SimpleTestGrammar GRAMMAR = new SimpleTestGrammar();
    private static final Sequence SEQUENCE = new Sequence("a b a b a", true);

    @Override
    public Sequence getSequence() {
        return SEQUENCE;
    }

    @Override
    public Grammar getGrammar() {
        return GRAMMAR.getGrammar();
    }

    @Override
    public void assertResult(CykResult cykResult) {
        assertRulesTable(cykResult.getRulesTable());
        assertTrue(cykResult.isParsed());
    }

    private void assertRulesTable(RulesTable rulesTable) {
        assertIterableEquals(extractRules(rulesTable, 0, 0), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 0, 1), Collections.singleton(GRAMMAR.B_b));
        assertIterableEquals(extractRules(rulesTable, 0, 2), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 0, 3), Collections.singleton(GRAMMAR.B_b));
        assertIterableEquals(extractRules(rulesTable, 0, 4), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 1, 0), Collections.singleton(GRAMMAR.C_AB));
        assertTrue(extractRules(rulesTable, 1, 1).size() == 1);
        assertIterableEquals(extractRules(rulesTable, 1, 2), Collections.singleton(GRAMMAR.C_AB));
        assertTrue(extractRules(rulesTable, 1, 3).size() == 1);
        assertTrue(extractRules(rulesTable, 2, 0).size() > 1);
        assertTrue(extractRules(rulesTable, 2, 1).size() > 1);
        assertTrue(extractRules(rulesTable, 2, 2).size() > 1);
        assertTrue(extractRules(rulesTable, 3, 0).size() > 1);
        assertTrue(extractRules(rulesTable, 3, 1).size() > 1);
        assertTrue(extractRules(rulesTable, 4, 0).size() > 1);
    }

}
