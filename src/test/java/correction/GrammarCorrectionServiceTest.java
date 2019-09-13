package correction;

import application.ApplicationException;
import common.AbstractServiceTest;
import common.util.TestUtils;
import grammar.Grammar;
import io.grammar.GrammarIoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GrammarCorrectionServiceTest extends AbstractServiceTest {

    private static final double DELTA = .000001;

    private GrammarCorrectionService correctionService = GrammarCorrectionService.getInstance();
    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    @Test
    public void testRemoveImproductiveRulesAndSymbols(){
        // given
        Grammar grammar = grammarIoService.parseGrammar("$->CC;C->AB;C->c", true, false);

        // when
        correctionService.correctGrammar(grammar);

        //assertThat
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getRule("C->AB"));
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getSymbol("A"));
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getSymbol("B"));
    }

    @Test
    public void testRemoveUnreachableRulesAndSymbols(){
        // given
        Grammar grammar = grammarIoService.parseGrammar("$->CC;B->CC;C->c", true, false);

        // when
        correctionService.correctGrammar(grammar);

        //assertThat
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getRule("B->CC"));
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getSymbol("B"));
    }

    @Test
    public void testCorrectGrammar(){
        // given
        Grammar grammar = grammarIoService.parseGrammar("$->CC(0.2);$->CD(0.3);B->CC(0);C->AA(0.6);C->c(0.4);D->d(1)", true, false);

        // when
        correctionService.correctGrammar(grammar);

        //assertThat
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getSymbol("A"));
        Assertions.assertThrows(ApplicationException.class, () -> grammar.getSymbol("B"));
        TestUtils.assertEquals(grammarIoService.parseGrammar("$->CC(0.2);$->CD(0.3);C->c(0.4);D->d(1)", false, false), grammar, DELTA);
    }

    @Test
    public void testNormalize(){
        // given
        Grammar grammar = grammarIoService.parseGrammar("A->a(0);B->b(0.6);C->c(0.4);$->AB(0.1);$->BC(0.3);$->A$(0.7);$->$C(0.5);A->BA(0);B->BB(0.01)", false, false);
        Grammar expectedGrammar = grammarIoService.parseGrammar("A->a(0.5);B->b(0.9836065);C->c(1);$->AB(0.0625);$->BC(0.1875);$->A$(0.4375);$->$C(0.3125);A->BA(0.5);B->BB(0.0163934)", false, false);

        // when
        correctionService.normalizeRules(grammar);

        // then
        TestUtils.assertEquals(expectedGrammar, grammar, DELTA);
    }

}
