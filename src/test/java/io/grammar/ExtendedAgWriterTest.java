package io.grammar;

import common.AbstractServiceTest;
import configuration.ConfigurationService;
import grammar.Grammar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ExtendedAgWriterTest extends AbstractServiceTest {

    private static final String AG_FORMAT = "grammar.format";

    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    @BeforeEach
    public void setUp() {
        ConfigurationService.getInstance().overrideProperty(AG_FORMAT, "EXTENDED");
    }

    @Test
    public void testWrite(){
        // given
        String input = "ALPHA -> 'alpha' (0.345678)\n" +
                "BETA -> 'beta'\n" +
                "GAMMA -> ALPHA BETA (0.5)\n" +
                "DELTA -> GAMMA ALPHA (0.7)\n" +
                "EPSYLON -> BETA GAMMA\n" +
                "PHI -> BETA DELTA\n" +
                "S -> ALPHA PHI";
        Grammar grammar = grammarIoService.parseGrammar(input, false, false);
        List<String> expected = Arrays.asList("ALPHA -> 'alpha' (0.345678)",
                "BETA -> 'beta' (1.0)",
                "GAMMA -> ALPHA BETA (0.5)",
                "DELTA -> GAMMA ALPHA (0.7)",
                "EPSYLON -> BETA GAMMA (1.0)",
                "PHI -> BETA DELTA (1.0)",
                "S -> ALPHA PHI (1.0)");

        // when
        String result = grammarIoService.writeGrammar(grammar);

        // then
        String[] rules = result.split("\n");
        Assertions.assertIterableEquals(
                Stream.of(rules).sorted().collect(Collectors.toList()),
                expected.stream().sorted().collect(Collectors.toList())
        );
    }

}