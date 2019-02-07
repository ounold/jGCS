package common.testCase;

import common.grammar.ArrowTestGrammar;
import common.grammar.TestGrammar;
import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Symbol;
import org.junit.jupiter.api.Assertions;
import probabilityArray.ProbabilityArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StochasticGrammarTestCase extends GrammarTestCase {

    private static final TestGrammar GRAMMAR = new ArrowTestGrammar("A->a(0.2);B->b(0.4);C->AB(0.5);D->CA(0.7);E->BC(0.1);F->BD(0.6);$->AF(0.9)");
    private static final Sequence SEQUENCE = new Sequence("a b a b a", true);

    private final Symbol A =  GRAMMAR.getGrammar().getSymbol("A");
    private final Symbol B =  GRAMMAR.getGrammar().getSymbol("B");
    private final Symbol C =  GRAMMAR.getGrammar().getSymbol("C");
    private final Symbol D =  GRAMMAR.getGrammar().getSymbol("D");
    private final Symbol E =  GRAMMAR.getGrammar().getSymbol("E");
    private final Symbol F =  GRAMMAR.getGrammar().getSymbol("F");
    private final Symbol S =  GRAMMAR.getGrammar().getSymbol("$");

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
        assertProbabilityArray(cykResult.getProbabilityArray());
        assertEquals(cykResult.getSentenceProbability(), .00024192, .000000001);
        assertTrue(cykResult.isParsed());
    }

    private void assertProbabilityArray(ProbabilityArray probabilityArray) {
        Assertions.assertEquals(extractProbability(probabilityArray, 0, 0, A), .2, .01);
        Assertions.assertEquals(extractProbability(probabilityArray, 0, 1, B), .4, .01);
        Assertions.assertEquals(extractProbability(probabilityArray, 0, 2, A), .2, .01);
        Assertions.assertEquals(extractProbability(probabilityArray, 0, 3, B), .4, .01);
        Assertions.assertEquals(extractProbability(probabilityArray, 0, 4, A), .2, .01);
        Assertions.assertEquals(extractProbability(probabilityArray, 1, 0, C), .04, .001);
        Assertions.assertEquals(extractProbability(probabilityArray, 1, 2, C), .04, .001);
        Assertions.assertEquals(extractProbability(probabilityArray, 2, 0, D), .0056, .00001);
        Assertions.assertEquals(extractProbability(probabilityArray, 2, 1, E), .0016, .00001);
        Assertions.assertEquals(extractProbability(probabilityArray, 2, 2, D), .0056, .00001);
        Assertions.assertEquals(extractProbability(probabilityArray, 3, 1, F), .001344, .0000001);
        Assertions.assertEquals(extractProbability(probabilityArray, 4, 0, S), .00024192, .000000001);
    }
}
