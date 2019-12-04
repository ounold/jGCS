package io.grammar;

import application.ApplicationException;
import common.AbstractServiceTest;
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

class ExtendedAgParserTest extends AbstractServiceTest {

    private static final String AG_FORMAT = "grammar.format";

    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    @BeforeEach
    public void setUp() {
        ConfigurationService.getInstance().overrideProperty(AG_FORMAT, "EXTENDED");
    }

    @Test
    public void testReadGrammar() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //when
        Grammar actual = grammarIoService.parseGrammar(input, false, false);

        //then
        assertSymbol(actual.getSymbol("alpha"), SymbolType.TERMINAL, 0);
        assertSymbol(actual.getSymbol("beta"), SymbolType.TERMINAL, 0);

        assertSymbol(actual.getSymbol("ALPHA"), SymbolType.NON_TERMINAL, 1);
        assertSymbol(actual.getSymbol("BETA"), SymbolType.NON_TERMINAL, 3);
        assertSymbol(actual.getSymbol("GAMMA"), SymbolType.NON_TERMINAL, 6);
        assertSymbol(actual.getSymbol("DELTA"), SymbolType.NON_TERMINAL, 4);
        assertSymbol(actual.getSymbol("EPSYLON"), SymbolType.NON_TERMINAL, 5);
        assertSymbol(actual.getSymbol("PHI"), SymbolType.NON_TERMINAL, 2);

        assertSymbol(actual.getSymbol("S"), SymbolType.START, 0);

        assertSortedStreams(Stream.of("ALPHA -> 'alpha'", "BETA -> 'beta'"), actual.getTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("ALPHA -> 'alpha'"), .345678d);
        assertRule(actual.getRule("BETA -> 'beta'"), 1d);

        assertSortedStreams(Stream.of("GAMMA -> ALPHA BETA", "DELTA -> GAMMA ALPHA", "EPSYLON -> BETA GAMMA", "PHI -> BETA DELTA", "S -> ALPHA PHI"), actual.getNonTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("GAMMA -> ALPHA BETA"), .5d);
        assertRule(actual.getRule("DELTA -> GAMMA ALPHA"), .7d);
        assertRule(actual.getRule("EPSYLON -> BETA GAMMA"), 1d);
        assertRule(actual.getRule("PHI -> BETA DELTA"), 1d);
        assertRule(actual.getRule("S -> ALPHA PHI"), 1d);
    }

    @Test
    public void testReadGrammarWithSkipDuplicates() {
        //given
        String input = "GAMMA -> ALPHA BETA (0.3)\n" +
                "ALPHA -> 'alpha' (0.5)\n" +
                "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //when
        Grammar actual = grammarIoService.parseGrammar(input, true, false);

        //then
        assertSymbol(actual.getSymbol("alpha"), SymbolType.TERMINAL, 0);
        assertSymbol(actual.getSymbol("beta"), SymbolType.TERMINAL, 0);

        assertSymbol(actual.getSymbol("ALPHA"), SymbolType.NON_TERMINAL, 1);
        assertSymbol(actual.getSymbol("BETA"), SymbolType.NON_TERMINAL, 3);
        assertSymbol(actual.getSymbol("GAMMA"), SymbolType.NON_TERMINAL, 6);
        assertSymbol(actual.getSymbol("DELTA"), SymbolType.NON_TERMINAL, 4);
        assertSymbol(actual.getSymbol("EPSYLON"), SymbolType.NON_TERMINAL, 5);
        assertSymbol(actual.getSymbol("PHI"), SymbolType.NON_TERMINAL, 2);

        assertSymbol(actual.getSymbol("S"), SymbolType.START, 0);

        assertSortedStreams(Stream.of("ALPHA -> 'alpha'", "BETA -> 'beta'"), actual.getTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("ALPHA -> 'alpha'"), .345678d);
        assertRule(actual.getRule("BETA -> 'beta'"), 1d);

        assertSortedStreams(Stream.of("GAMMA -> ALPHA BETA", "DELTA -> GAMMA ALPHA", "EPSYLON -> BETA GAMMA", "PHI -> BETA DELTA", "S -> ALPHA PHI"), actual.getNonTerminalRules().stream().map(Rule::toString));
        assertRule(actual.getRule("GAMMA -> ALPHA BETA"), .5d);
        assertRule(actual.getRule("DELTA -> GAMMA ALPHA"), .7d);
        assertRule(actual.getRule("EPSYLON -> BETA GAMMA"), 1d);
        assertRule(actual.getRule("PHI -> BETA DELTA"), 1d);
        assertRule(actual.getRule("S -> ALPHA PHI"), 1d);
    }

    @Test
    public void testReadGrammarForInvalidTerminalSymbol() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> ''\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidNonTerminalSymbol() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ! BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidTerminalRule() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA ->'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForInvalidNonTerminalRule() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHABETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedTerminalRule() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "BETA -> 'beta'\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedNonTerminalRule() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "S -> ALPHA PHI";

        //then
        assertThrows(ApplicationException.class, () -> grammarIoService.parseGrammar(input, false, false));
    }

    @Test
    public void testReadGrammarForDuplicatedNoStartSymbol() {
        //given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "SIGMA -> ALPHA PHI";

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