package common.testCase;

import common.grammar.ArrowTestGrammar;
import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import probabilityArray.ProbabilityArray;
import rulesTable.Coordinate;
import rulesTable.RulesTable;

import static org.junit.jupiter.api.Assertions.*;

public class MutlipleStochasticTestCase extends GrammarTestCase {

    protected static final double DELTA = .000001;

    private static final Grammar GRAMMAR = new ArrowTestGrammar("A->a(0.2);B->b(0.6);C->c(0.4);$->AB(0.1);$->BC(0.3);$->A$(0.7);$->$C(0.5)").getGrammar();
    private static final Sequence SEQUENCE = new Sequence("a b c", true);

    protected final Symbol A = GRAMMAR.getSymbol("A");
    protected final Symbol B = GRAMMAR.getSymbol("B");
    protected final Symbol C = GRAMMAR.getSymbol("C");
    protected final Symbol S = GRAMMAR.getSymbol("$");

    protected static final Rule A_a = GRAMMAR.getRule("A -> 'a'");
    protected static final Rule B_b = GRAMMAR.getRule("B -> 'b'");
    protected static final Rule C_c = GRAMMAR.getRule("C -> 'c'");
    protected static final Rule S_AB = GRAMMAR.getRule("$ -> A B");
    protected static final Rule S_BC = GRAMMAR.getRule("$ -> B C");
    protected static final Rule S_AS = GRAMMAR.getRule("$ -> A $");
    protected static final Rule S_SC = GRAMMAR.getRule("$ -> $ C");

    @Override
    public Sequence getSequence() {
        return SEQUENCE;
    }

    @Override
    public Grammar getGrammar() {
        return GRAMMAR;
    }

    @Override
    public void assertResult(CykResult cykResult) {
        assertRulesTable(cykResult.getRulesTable());
        assertProbabilityArray(cykResult.getProbabilityArray());
        assertEquals(cykResult.getSentenceProbability(), .01248, DELTA);
        assertTrue(cykResult.isParsed());
    }

    protected void assertRulesTable(RulesTable rulesTable) {
        assertNull(rulesTable.getCellRule(0, 0, A_a).getCell1Coordinates());
        assertNull(rulesTable.getCellRule(0, 0, A_a).getCell2Coordinates());
        assertNull(rulesTable.getCellRule(0, 1, B_b).getCell1Coordinates());
        assertNull(rulesTable.getCellRule(0, 1, B_b).getCell2Coordinates());
        assertNull(rulesTable.getCellRule(0, 2, C_c).getCell1Coordinates());
        assertNull(rulesTable.getCellRule(0, 2, C_c).getCell2Coordinates());
        assertEquals(rulesTable.getCellRule(1, 0, S_AB).getCell1Coordinates(), new Coordinate(0, 0));
        assertEquals(rulesTable.getCellRule(1, 0, S_AB).getCell2Coordinates(), new Coordinate(0, 1));
        assertEquals(rulesTable.getCellRule(1, 1, S_BC).getCell1Coordinates(), new Coordinate(0, 1));
        assertEquals(rulesTable.getCellRule(1, 1, S_BC).getCell2Coordinates(), new Coordinate(0, 2));
        assertEquals(rulesTable.getCellRule(2, 0, S_AS).getCell1Coordinates(), new Coordinate(0, 0));
        assertEquals(rulesTable.getCellRule(2, 0, S_AS).getCell2Coordinates(), new Coordinate(1, 1));
        assertEquals(rulesTable.getCellRule(2, 0, S_SC).getCell1Coordinates(), new Coordinate(1, 0));
        assertEquals(rulesTable.getCellRule(2, 0, S_SC).getCell2Coordinates(), new Coordinate(0, 2));
    }

    protected void assertProbabilityArray(ProbabilityArray probabilityArray) {
        assertEquals(extractProbability(probabilityArray, 0, 0, A), .2, .01);
        assertEquals(extractProbability(probabilityArray, 0, 1, B), .6, .01);
        assertEquals(extractProbability(probabilityArray, 0, 2, C), .4, .01);
        assertEquals(extractProbability(probabilityArray, 1, 0, S), .012, .0001);
        assertEquals(extractProbability(probabilityArray, 1, 1, S), .072, .0001);
        assertEquals(extractProbability(probabilityArray, 1, 1, S), .072, .0001);
        assertEquals(extractProbability(probabilityArray, 2, 0, S), .01248, .000001);
    }

}
