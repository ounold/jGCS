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

class SimpleAgWriterTest extends AbstractServiceTest {

    private static final String AG_FORMAT = "grammar.format";

    private GrammarIoService grammarIoService = GrammarIoService.getInstance();

    @BeforeEach
    public void setUp() {
        ConfigurationService.getInstance().overrideProperty(AG_FORMAT, "SIMPLE");
    }

    @Test
    public void testWrite(){
        // given
        String input = "A->a(0.345678);\n" +
                "B->b; C->AB(0.5);D ->CA(0.7);E->\t BC;F->B D;$->\r\n" +
                "AF ";
        Grammar grammar = grammarIoService.parseGrammar(input, false, false);
        List<String> expected = Arrays.asList("A->a(0.345678)", "B->b(1.0)", "C->AB(0.5)", "D->CA(0.7)", "E->BC(1.0)", "F->BD(1.0)", "$->AF(1.0)");

        // when
        String result = grammarIoService.writeGrammar(grammar);

        // then
        String[] rules = result.split(";");
        Assertions.assertIterableEquals(
                Stream.of(rules).sorted().collect(Collectors.toList()),
                expected.stream().sorted().collect(Collectors.toList())
        );
    }

}