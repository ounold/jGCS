package io.grammar;

import application.ApplicationException;
import configuration.ConfigurationService;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleAgParserTest {

    private static final String AG_FORMAT = "grammar.format";

    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    @BeforeEach
    public void setUp(){
        ConfigurationService.getInstance().overrideProperty(AG_FORMAT, "SIMPLE");
    }

    @Test
    public void testReadGrammar() {
        //given
        String input = "A->a(0.345678);\t" +
                "B->b; C->AB(0.5);D\n ->CA(0.7);E-> BC;F->B D;$->" +
                "AF ";

        //when
        Grammar actual = grammarIoService.parseGrammar(input, false, false);

        //then
        assertSymbol(actual.getSymbol("a"), SymbolType.TERMINAL, 0);
        assertSymbol(actual.getSymbol("b"), SymbolType.TERMINAL, 0);

        assertSymbol(actual.getSymbol("A"), SymbolType.NON_TERMINAL, 1);
        assertSymbol(actual.getSymbol("B"), SymbolType.NON_TERMINAL, 3);
        assertSymbol(actual.getSymbol("C"), SymbolType.NON_TERMINAL, 6);
        assertSymbol(actual.getSymbol("D"), SymbolType.NON_TERMINAL, 4);
        assertSymbol(actual.getSymbol("E"), SymbolType.NON_TERMINAL, 5);
        assertSymbol(actual.getSymbol("F"), SymbolType.NON_TERMINAL, 2);

        assertSymbol(actual.getSymbol("$"), SymbolType.START, 0);

        assertSortedStreams(Stream.of("A -> 'a'", "B -> 'b'"), actual.getTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("A -> 'a'"), .345678d);
        assertRule(actual.getRule("B -> 'b'"), 1d);

        assertSortedStreams(Stream.of("C -> A B", "D -> C A", "E -> B C", "F -> B D", "$ -> A F"), actual.getNonTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("C -> A B"), .5d);
        assertRule(actual.getRule("D -> C A"), .7d);
        assertRule(actual.getRule("E -> B C"), 1d);
        assertRule(actual.getRule("F -> B D"), 1d);
        assertRule(actual.getRule("$ -> A F"), 1d);
    }

    @Test
    public void testReadGrammarWithSkipDuplicates() {
        //given
        String input = "C->AB(0.3); A->a(0.5);A->a(0.345678);\t" +
                "B->b; C->AB(0.5);D\n ->CA(0.7);E-> BC;F->B D;$->" +
                "AF ";

        //when
        Grammar actual = grammarIoService.parseGrammar(input, true, false);

        //then
        assertSymbol(actual.getSymbol("a"), SymbolType.TERMINAL, 0);
        assertSymbol(actual.getSymbol("b"), SymbolType.TERMINAL, 0);

        assertSymbol(actual.getSymbol("A"), SymbolType.NON_TERMINAL, 1);
        assertSymbol(actual.getSymbol("B"), SymbolType.NON_TERMINAL, 3);
        assertSymbol(actual.getSymbol("C"), SymbolType.NON_TERMINAL, 6);
        assertSymbol(actual.getSymbol("D"), SymbolType.NON_TERMINAL, 4);
        assertSymbol(actual.getSymbol("E"), SymbolType.NON_TERMINAL, 5);
        assertSymbol(actual.getSymbol("F"), SymbolType.NON_TERMINAL, 2);

        assertSymbol(actual.getSymbol("$"), SymbolType.START, 0);

        assertSortedStreams(Stream.of("A -> 'a'", "B -> 'b'"), actual.getTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("A -> 'a'"), .345678d);
        assertRule(actual.getRule("B -> 'b'"), 1d);

        assertSortedStreams(Stream.of("C -> A B", "D -> C A", "E -> B C", "F -> B D", "$ -> A F"), actual.getNonTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("C -> A B"), .5d);
        assertRule(actual.getRule("D -> C A"), .7d);
        assertRule(actual.getRule("E -> B C"), 1d);
        assertRule(actual.getRule("F -> B D"), 1d);
        assertRule(actual.getRule("$ -> A F"), 1d);
    }

    @Test
    public void testReadGrammarForInvalidTerminalSymbol() {
        //given
        String input = "A->!(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;$->AF";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidNonTerminalSymbol() {
        //given
        String input = "A->a(0.345678);B->b;C->!B(0.5);D->CA(0.7);E->BC;F->BD;$->AF";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidTerminalRule() {
        //given
        String input = "A->A(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;$->AF";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidNonTerminalRule() {
        //given
        String input = "A->a(0.345678);B->b;C->aB(0.5);D->CA(0.7);E->BC;F->BD;$->AF";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedTerminalRule() {
        //given
        String input = "A->a(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;$->AF;A->a(0.3)";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedNonTerminalRule() {
        //given
        String input = "A->a(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;$->AF;C->AB";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedNoStartSymbol() {
        //given
        String input = "A->a(0.345678);B->b;C->AB(0.5);D->CA(0.7);E->BC;F->BD;S->AF";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }


    private void assertSymbol(Symbol symbol, SymbolType type, int index) {
        assertEquals(symbol.getSymbolType(), type);
        assertEquals(symbol.getIndex(), index);
    }

    private void assertRule(Rule rule, double probability) {
        assertEquals(rule.getProbability(), probability);
    }


    private <T> void assertSortedStreams(Stream<T> expected, Stream<T> actual) {
        assertIterableEquals(
                expected.sorted().collect(Collectors.toList()),
                actual.sorted().collect(Collectors.toList())
        );
    }

}