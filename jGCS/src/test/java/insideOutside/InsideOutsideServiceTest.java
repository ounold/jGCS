package insideOutside;

import configuration.ConfigurationService;
import cyk.CykResult;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import io.grammar.GrammarIoService;
import org.junit.jupiter.api.Test;
import probabilityArray.ProbabilityArray;
import rulesTable.Coordinate;
import rulesTable.PureCellRule;
import rulesTable.RulesTable;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class InsideOutsideServiceTest extends AbstractInsideOutsideTest {

    private static final String GRAMMAR = "A->a(0.2);B->b(0.6);C->c(0.4);$->AB(0.1);$->BC(0.3);$->A$(0.7);$->$C(0.5);A->BA(1);B->BB(0.01)";

    private static final Function<Grammar, Rule> A_a = g -> g.getRule("A -> 'a'");
    private static final Function<Grammar, Rule> B_b = g -> g.getRule("B -> 'b'");
    private static final Function<Grammar, Rule> C_c = g -> g.getRule("C -> 'c'");
    private static final Function<Grammar, Rule> S_AB = g -> g.getRule("$ -> A B");
    private static final Function<Grammar, Rule> S_BC = g -> g.getRule("$ -> B C");
    private static final Function<Grammar, Rule> S_AS = g -> g.getRule("$ -> A $");
    private static final Function<Grammar, Rule> S_SC = g -> g.getRule("$ -> $ C");
    private static final Function<Grammar, Rule> A_BA = g -> g.getRule("A -> B A");
    private static final Function<Grammar, Rule> B_BB = g -> g.getRule("B -> B B");

    private static final Function<Grammar, Symbol> A = g -> g.getSymbol("A");
    private static final Function<Grammar, Symbol> B = g -> g.getSymbol("B");
    private static final Function<Grammar, Symbol> C = g -> g.getSymbol("C");
    private static final Function<Grammar, Symbol> S = g -> g.getSymbol("$");

    private Grammar grammar;

    private InsideOutsideService insideOutsideService = InsideOutsideService.getInstance();
    private GrammarIoService grammarIoService = GrammarIoService.getInstance();


    public void initGrammar() {
        grammar = grammarIoService.parseGrammar(GRAMMAR, false, false);
        ConfigurationService.getInstance().overrideProperty("ce.mode", "IO");
    }

    @Test
    public void testInsideProbabilities() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", true), 0));

        RulesTable rulesTable = cykResult.getRulesTable();
        assertEquals(extractInside(rulesTable, 0, 0, A_a.apply(grammar)), .2, DELTA);
        assertEquals(extractInside(rulesTable, 0, 1, B_b.apply(grammar)), .6, DELTA);
        assertEquals(extractInside(rulesTable, 0, 2, C_c.apply(grammar)), .4, DELTA);
        assertEquals(extractInside(rulesTable, 1, 0, S_AB.apply(grammar)), .012, DELTA);
        assertEquals(extractInside(rulesTable, 1, 1, S_BC.apply(grammar)), .072, DELTA);
        assertEquals(extractInside(rulesTable, 2, 0, S_AS.apply(grammar)), .01248, DELTA);
        assertEquals(extractInside(rulesTable, 2, 0, S_SC.apply(grammar)), .01248, DELTA);
    }

    @Test
    public void testOutsideProbabilites() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", true), 0));

        RulesTable rulesTable = cykResult.getRulesTable();
        assertEquals(extractOutside(rulesTable, 0, 0, A_a.apply(grammar)), .0624, DELTA);
        assertEquals(extractOutside(rulesTable, 0, 1, B_b.apply(grammar)), .0208, DELTA);
        assertEquals(extractOutside(rulesTable, 0, 2, C_c.apply(grammar)), .0312, DELTA);
        assertEquals(extractOutside(rulesTable, 1, 0, S_AB.apply(grammar)), .2, DELTA);
        assertEquals(extractOutside(rulesTable, 1, 1, S_BC.apply(grammar)), .14, DELTA);
        assertEquals(extractOutside(rulesTable, 2, 0, S_AS.apply(grammar)), 1, DELTA);
        assertEquals(extractOutside(rulesTable, 2, 0, S_SC.apply(grammar)), 1, DELTA);
    }

    @Test
    public void testInsideOutsideProbabilitesForPositiveSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", true), 0));

        assertInsideOutsideValues(A_a.apply(grammar), .0624, 0, 1);
        assertInsideOutsideValues(B_b.apply(grammar), .0208, 0, 1);
        assertInsideOutsideValues(C_c.apply(grammar), .0312, 0, 1);
        assertInsideOutsideValues(S_AB.apply(grammar), .024, 0, 1);
        assertInsideOutsideValues(S_BC.apply(grammar), .0336, 0, 1);
        assertInsideOutsideValues(S_AS.apply(grammar), .0144, 0, 1);
        assertInsideOutsideValues(S_SC.apply(grammar), .0048, 0, 1);
        assertInsideOutsideValues(A_BA.apply(grammar), 0, 0, 0);
        assertInsideOutsideValues(B_BB.apply(grammar), 0, 0, 0);
    }

    @Test
    public void testInsideOutsideProbabilitesForNegativeSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", false), 0));

        assertInsideOutsideValues(A_a.apply(grammar), 0, .0624, 1);
        assertInsideOutsideValues(B_b.apply(grammar), 0, .0208, 1);
        assertInsideOutsideValues(C_c.apply(grammar), 0, .0312, 1);
        assertInsideOutsideValues(S_AB.apply(grammar), 0, .024, 1);
        assertInsideOutsideValues(S_BC.apply(grammar), 0, .0336, 1);
        assertInsideOutsideValues(S_AS.apply(grammar), 0, .0144, 1);
        assertInsideOutsideValues(S_SC.apply(grammar), 0, .0048, 1);
        assertInsideOutsideValues(A_BA.apply(grammar), 0, 0, 0);
        assertInsideOutsideValues(B_BB.apply(grammar), 0, 0, 0);
    }

    @Test
    public void testRulesCountsForPositiveSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", true), 0));

        assertRulesCounts(A_a.apply(grammar), 1, 1, 0);
        assertRulesCounts(B_b.apply(grammar), 1, 1, 0);
        assertRulesCounts(C_c.apply(grammar), 1, 1, 0);
        assertRulesCounts(S_AB.apply(grammar), 0.19230769, 0.19230769, 0);
        assertRulesCounts(S_BC.apply(grammar), 0.80769230, 0.80769230, 0);
        assertRulesCounts(S_AS.apply(grammar), 0.80769230, 0.80769230, 0);
        assertRulesCounts(S_SC.apply(grammar), 0.19230769, 0.19230769, 0);
        assertRulesCounts(A_BA.apply(grammar), 0, 0, 0);
        assertRulesCounts(B_BB.apply(grammar), 0, 0, 0);
    }

    @Test
    public void testRulesCountsForNegativeSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", false), 0));

        assertRulesCounts(A_a.apply(grammar), 1, 0, 1);
        assertRulesCounts(B_b.apply(grammar), 1, 0, 1);
        assertRulesCounts(C_c.apply(grammar), 1, 0, 1);
        assertRulesCounts(S_AB.apply(grammar), 0.19230769, 0, 0.19230769);
        assertRulesCounts(S_BC.apply(grammar), 0.80769230, 0, 0.80769230);
        assertRulesCounts(S_AS.apply(grammar), 0.80769230, 0, 0.80769230);
        assertRulesCounts(S_SC.apply(grammar), 0.19230769, 0, 0.19230769);
        assertRulesCounts(A_BA.apply(grammar), 0, 0, 0);
        assertRulesCounts(B_BB.apply(grammar), 0, 0, 0);
    }

    @Test
    public void testUpdateRulesProbabilitiesForPositiveSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", true), 0));
        insideOutsideService.updateRulesProbabilities(grammar);

        // Updated
        assertEquals(A_a.apply(grammar).getProbability(), .4, DELTA);
        assertEquals(B_b.apply(grammar).getProbability(), .7, DELTA);
        assertEquals(C_c.apply(grammar).getProbability(), .55, DELTA);
        assertEquals(S_AB.apply(grammar).getProbability(), .099038461490385, DELTA);
        assertEquals(S_BC.apply(grammar).getProbability(), .325961538509615, DELTA);
        assertEquals(S_AS.apply(grammar).getProbability(), .625961538509615, DELTA);
        assertEquals(S_SC.apply(grammar).getProbability(), .399038461490385, DELTA);

        // Not updated
        assertEquals(A_BA.apply(grammar).getProbability(), 1, .001);
        assertEquals(B_BB.apply(grammar).getProbability(), .01, .001);
    }

    @Test
    public void testUpdateRulesProbabilitiesForNegativeSentence() {
        CykResult cykResult = buildCykResult();
        insideOutsideService.updateRulesCounts(grammar, cykResult, new IoSequence(new Sequence("a b c", false), 0));
        insideOutsideService.updateRulesProbabilities(grammar);

        // Updated
        assertEquals(A_a.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(B_b.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(C_c.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(S_AB.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(S_BC.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(S_AS.apply(grammar).getProbability(), 0., DELTA);
        assertEquals(S_SC.apply(grammar).getProbability(), 0., DELTA);

        // Not updated
        assertEquals(A_BA.apply(grammar).getProbability(), 1, .001);
        assertEquals(B_BB.apply(grammar).getProbability(), .01, .001);
    }

    private CykResult buildCykResult() {
        RulesTable rulesTable = new RulesTable(3);
        rulesTable.getCellRules(0, 0).add(new PureCellRule(A_a.apply(grammar), new Coordinate(0, 0)));
        rulesTable.getCellRules(0, 1).add(new PureCellRule(B_b.apply(grammar), new Coordinate(0, 1)));
        rulesTable.getCellRules(0, 2).add(new PureCellRule(C_c.apply(grammar), new Coordinate(0, 2)));
        rulesTable.getCellRules(1, 0).add(new PureCellRule(S_AB.apply(grammar), new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 0)));
        rulesTable.getCellRules(1, 1).add(new PureCellRule(S_BC.apply(grammar), new Coordinate(0, 1), new Coordinate(0, 2), new Coordinate(1, 1)));
        rulesTable.getCellRules(2, 0).add(new PureCellRule(S_AS.apply(grammar), new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 0)));
        rulesTable.getCellRules(2, 0).add(new PureCellRule(S_SC.apply(grammar), new Coordinate(1, 0), new Coordinate(0, 2), new Coordinate(2, 0)));


        ProbabilityArray probabilityArray = new ProbabilityArray(3, grammar.getNonTerminalSymbols().size());
        probabilityArray.add(0, 0, A.apply(grammar), .2);
        probabilityArray.add(0, 1, B.apply(grammar), .6);
        probabilityArray.add(0, 2, C.apply(grammar), .4);
        probabilityArray.add(1, 0, S.apply(grammar), .012);
        probabilityArray.add(1, 1, S.apply(grammar), .072);
        probabilityArray.add(2, 0, S.apply(grammar), .01248);

        return new CykResult(rulesTable, probabilityArray, .01248, true);
    }


}