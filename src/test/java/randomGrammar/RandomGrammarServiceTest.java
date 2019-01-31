package randomGrammar;

import application.ApplicationException;
import common.AbstractServiceTest;
import dataset.Dataset;
import dataset.Sequence;
import grammar.Grammar;
import grammar.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class RandomGrammarServiceTest extends AbstractServiceTest {

    private RandomGrammarService randomGrammarService = RandomGrammarService.getInstance();

    @Test
    public void shouldGenerateGrammarWithBoundedTerminals(){
        // given
        Dataset dataset = new Dataset(
                Arrays.asList(
                        new Sequence("a b c", true),
                        new Sequence("a b d", true),
                        new Sequence("e a f a b c a b c", true),
                        new Sequence("d e a", true),
                        new Sequence("d d a b c d", true),
                        new Sequence("d a b c b a", true),
                        new Sequence("g e a", true),
                        new Sequence("h e a d e a", true),
                        new Sequence("i i i i i i i", true),
                        new Sequence("i j i j i j", true),
                        new Sequence("k a b c l m", true)
                )
        );

        // when
        Grammar grammar = randomGrammarService.createRandomGrammar(dataset);

        // then
        Assertions.assertEquals(grammar.getTerminalSymbols().size(), 10);
        String expectedTerminals = "a b c d e f g h i j k l m";
        String expectedNonTerminals = expectedTerminals.toUpperCase() + "$";
        Assertions.assertTrue(grammar.getTerminalSymbols().stream().map(Symbol::getValue).map(Object::toString).allMatch(expectedTerminals::contains));
        Assertions.assertEquals(grammar.getNonTerminalSymbols().size(), 10 + 1);
        Assertions.assertTrue(grammar.getNonTerminalSymbols().stream().map(Symbol::getValue).map(Object::toString).allMatch(expectedNonTerminals::contains));
        Assertions.assertEquals(grammar.getTerminalRules().size(), 10);
        Assertions.assertEquals(grammar.getNonTerminalRules().size(), 10);
    }

    @Test
    public void shouldGenerateGrammar(){
        // given
        Dataset dataset = new Dataset(
                Arrays.asList(
                        new Sequence("a b c", true),
                        new Sequence("a b d", true),
                        new Sequence("e e", true)
                )
        );

        // when
        Grammar grammar = randomGrammarService.createRandomGrammar(dataset);

        // then
        Assertions.assertEquals(grammar.getTerminalSymbols().size(), 5);
        String expectedTerminals = "a b c d e";
        String expectedNonTerminals = expectedTerminals.toUpperCase() + "$";
        Assertions.assertTrue(grammar.getTerminalSymbols().stream().map(Symbol::getValue).map(Object::toString).allMatch(expectedTerminals::contains));
        Assertions.assertEquals(grammar.getNonTerminalSymbols().size(), 5 + 1);
        Assertions.assertTrue(grammar.getNonTerminalSymbols().stream().map(Symbol::getValue).map(Object::toString).allMatch(expectedNonTerminals::contains));
        Assertions.assertEquals(grammar.getTerminalRules().size(), 5);
        Assertions.assertEquals(grammar.getNonTerminalRules().size(), 10);
    }

    @Test
    public void shouldFailIfImpossibleNumberOfNonTerminals(){
        // given
        Dataset dataset = new Dataset(
                Arrays.asList(
                        new Sequence("a", true)
                )
        );

        // then
        Assertions.assertThrows(ApplicationException.class, () -> randomGrammarService.createRandomGrammar(dataset));
    }

}