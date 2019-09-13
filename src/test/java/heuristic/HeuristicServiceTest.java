package heuristic;

import common.AbstractServiceTest;
import common.grammar.SimpleTestGrammar;
import common.grammar.TestGrammar;
import common.mock.RandomMock;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;
import heurstic.Heuristic;
import heurstic.HeuristicService;
import heurstic.splitAndMerge.SplitAndMerge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HeuristicServiceTest extends AbstractServiceTest {

    private HeuristicService heuristicService;

    @BeforeEach
    public void initialize() {
        heuristicService = HeuristicService.getInstance();
    }

    @Test
    public void testMerge() throws NoSuchFieldException, IllegalAccessException {
        Random randomMock = new RandomMock(Arrays.asList(1, 2));

        Heuristic heuristic = new SplitAndMerge();
        Field random = heuristic.getClass().getDeclaredField("random");
        random.setAccessible(true);
        random.set(heuristic, randomMock);

        Field algorithm = heuristicService.getClass().getDeclaredField("algorithm");
        algorithm.setAccessible(true);
        algorithm.set(heuristicService, heuristic);

        TestGrammar simpleTestGrammar = new SimpleTestGrammar();
        Grammar grammar = simpleTestGrammar.getGrammar();

        heuristicService.run(grammar, 0);

        Symbol B = new Symbol("B", 0, SymbolType.NON_TERMINAL);
        Symbol C = new Symbol("C", 0, SymbolType.NON_TERMINAL);
        Symbol D = new Symbol("D", 0, SymbolType.NON_TERMINAL);
        Symbol E = new Symbol("E", 0, SymbolType.NON_TERMINAL);
        Symbol F = new Symbol("F", 0, SymbolType.NON_TERMINAL);
        Symbol S = new Symbol("$", 0, SymbolType.NON_TERMINAL);

        List<Rule> rules = grammar.getRules();
        Assertions.assertEquals(rules.get(0).getLeft(), B);
        Assertions.assertEquals(rules.get(0).getRight1(), new Symbol("a", 0, SymbolType.TERMINAL));
        Assertions.assertEquals(rules.get(1).getLeft(), B);
        Assertions.assertEquals(rules.get(1).getRight1(), new Symbol("b", 0, SymbolType.TERMINAL));
        Assertions.assertEquals(rules.get(2).getLeft(), C);
        Assertions.assertEquals(rules.get(2).getRight1(), B);
        Assertions.assertEquals(rules.get(2).getRight2(), B);
        Assertions.assertEquals(rules.get(3).getLeft(), D);
        Assertions.assertEquals(rules.get(3).getRight1(), C);
        Assertions.assertEquals(rules.get(3).getRight2(), B);
        Assertions.assertEquals(rules.get(4).getLeft(), E);
        Assertions.assertEquals(rules.get(4).getRight1(), B);
        Assertions.assertEquals(rules.get(4).getRight2(), C);
        Assertions.assertEquals(rules.get(5).getLeft(), F);
        Assertions.assertEquals(rules.get(5).getRight1(), B);
        Assertions.assertEquals(rules.get(5).getRight2(), D);
        Assertions.assertEquals(rules.get(6).getLeft(), S);
        Assertions.assertEquals(rules.get(6).getRight1(), B);
        Assertions.assertEquals(rules.get(6).getRight2(), F);
    }

    @Test
    public void testSplit() throws IllegalAccessException, NoSuchFieldException {
        Random randomMock = new RandomMock(Arrays.asList(1, 0, 1));

        Heuristic heuristic = new SplitAndMerge();
        Field random = heuristic.getClass().getDeclaredField("random");
        random.setAccessible(true);
        random.set(heuristic, randomMock);

        Field algorithm = heuristicService.getClass().getDeclaredField("algorithm");
        algorithm.setAccessible(true);
        algorithm.set(heuristicService, heuristic);

        TestGrammar simpleTestGrammar = new SimpleTestGrammar();
        Grammar grammar = simpleTestGrammar.getGrammar();

        heuristicService.run(grammar, 3);

        Symbol B = new Symbol("B", 0, SymbolType.NON_TERMINAL);
        Symbol C = new Symbol("C", 0, SymbolType.NON_TERMINAL);
        Symbol D = new Symbol("D", 0, SymbolType.NON_TERMINAL);
        Symbol E = new Symbol("E", 0, SymbolType.NON_TERMINAL);
        Symbol F = new Symbol("F", 0, SymbolType.NON_TERMINAL);
        Symbol G = new Symbol("G", 0, SymbolType.NON_TERMINAL);
        Symbol H = new Symbol("H", 0, SymbolType.NON_TERMINAL);
        Symbol S = new Symbol("$", 0, SymbolType.NON_TERMINAL);

        List<Rule> rules = grammar.getRules();
        Assertions.assertEquals(rules.size(), 11);

        Assertions.assertEquals(rules.get(0).getLeft(), B);
        Assertions.assertEquals(rules.get(0).getRight1(), new Symbol("b", 0, SymbolType.TERMINAL));

        Assertions.assertEquals(rules.get(1).getLeft(), G);
        Assertions.assertEquals(rules.get(1).getRight1(), new Symbol("a", 0, SymbolType.TERMINAL));

        Assertions.assertEquals(rules.get(2).getLeft(), H);
        Assertions.assertEquals(rules.get(2).getRight1(), new Symbol("a", 0, SymbolType.TERMINAL));

        Assertions.assertEquals(rules.get(3).getLeft(), E);
        Assertions.assertEquals(rules.get(3).getRight1(), B);
        Assertions.assertEquals(rules.get(3).getRight2(), C);

        Assertions.assertEquals(rules.get(4).getLeft(), F);
        Assertions.assertEquals(rules.get(4).getRight1(), B);
        Assertions.assertEquals(rules.get(4).getRight2(), D);

        Assertions.assertEquals(rules.get(5).getLeft(), C);
        Assertions.assertEquals(rules.get(5).getRight1(), G);
        Assertions.assertEquals(rules.get(5).getRight2(), B);

        Assertions.assertEquals(rules.get(6).getLeft(), C);
        Assertions.assertEquals(rules.get(6).getRight1(), H);
        Assertions.assertEquals(rules.get(6).getRight2(), B);

        Assertions.assertEquals(rules.get(7).getLeft(), S);
        Assertions.assertEquals(rules.get(7).getRight1(), G);
        Assertions.assertEquals(rules.get(7).getRight2(), F);

        Assertions.assertEquals(rules.get(8).getLeft(), S);
        Assertions.assertEquals(rules.get(8).getRight1(), H);
        Assertions.assertEquals(rules.get(8).getRight2(), F);

        Assertions.assertEquals(rules.get(9).getLeft(), D);
        Assertions.assertEquals(rules.get(9).getRight1(), C);
        Assertions.assertEquals(rules.get(9).getRight2(), G);

        Assertions.assertEquals(rules.get(10).getLeft(), D);
        Assertions.assertEquals(rules.get(10).getRight1(), C);
        Assertions.assertEquals(rules.get(10).getRight2(), H);
    }

}
