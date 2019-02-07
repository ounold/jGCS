package common.testCase;

import common.grammar.SimpleTestGrammar;
import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import probabilityArray.ProbabilityArray;
import rulesTable.RulesTable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleGrammarTestCase extends GrammarTestCase {
    
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
        assertProbabilityArray(cykResult.getProbabilityArray());
        assertEquals(cykResult.getSentenceProbability(), 1, .1);
        assertTrue(cykResult.isParsed());
    }

    private void assertRulesTable(RulesTable rulesTable) {
        assertIterableEquals(extractRules(rulesTable, 0, 0), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 0, 1), Collections.singleton(GRAMMAR.B_b));
        assertIterableEquals(extractRules(rulesTable, 0, 2), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 0, 3), Collections.singleton(GRAMMAR.B_b));
        assertIterableEquals(extractRules(rulesTable, 0, 4), Collections.singleton(GRAMMAR.A_a));
        assertIterableEquals(extractRules(rulesTable, 1, 0), Collections.singleton(GRAMMAR.C_AB));
        assertIterableEquals(extractRules(rulesTable, 1, 1), Collections.emptyList());
        assertIterableEquals(extractRules(rulesTable, 1, 2), Collections.singleton(GRAMMAR.C_AB));
        assertIterableEquals(extractRules(rulesTable, 1, 3), Collections.emptyList());
        assertIterableEquals(extractRules(rulesTable, 2, 0), Collections.singleton(GRAMMAR.D_CA));
        assertIterableEquals(extractRules(rulesTable, 2, 1), Collections.singleton(GRAMMAR.E_BC));
        assertIterableEquals(extractRules(rulesTable, 2, 2), Collections.singleton(GRAMMAR.D_CA));
        assertIterableEquals(extractRules(rulesTable, 3, 0), Collections.emptyList());
        assertIterableEquals(extractRules(rulesTable, 3, 1), Collections.singleton(GRAMMAR.F_BD));
        assertIterableEquals(extractRules(rulesTable, 4, 0), Collections.singleton(GRAMMAR.S_AF));
    }

    private void assertProbabilityArray(ProbabilityArray probabilityArray){
        assertEquals(extractProbability(probabilityArray, 0, 0, GRAMMAR.A), 1d);
        assertEquals(extractProbability(probabilityArray, 0, 1, GRAMMAR.B), 1d);
        assertEquals(extractProbability(probabilityArray, 0, 2, GRAMMAR.A), 1d);
        assertEquals(extractProbability(probabilityArray, 0, 3, GRAMMAR.B), 1d);
        assertEquals(extractProbability(probabilityArray, 0, 4, GRAMMAR.A), 1d);
        assertEquals(extractProbability(probabilityArray, 1, 0, GRAMMAR.C), 1d);
        assertEquals(extractProbability(probabilityArray, 1, 2, GRAMMAR.C), 1d);
        assertEquals(extractProbability(probabilityArray, 2, 0, GRAMMAR.D), 1d);
        assertEquals(extractProbability(probabilityArray, 2, 1, GRAMMAR.E), 1d);
        assertEquals(extractProbability(probabilityArray, 2, 2, GRAMMAR.D), 1d);
        assertEquals(extractProbability(probabilityArray, 3, 1, GRAMMAR.F), 1d);
        assertEquals(extractProbability(probabilityArray, 4, 0, GRAMMAR.S), 1d);
    }
}
