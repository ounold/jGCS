package covering;

import common.AbstractServiceTest;
import common.grammar.SimpleTestGrammar;
import common.grammar.TestGrammar;
import common.mock.RandomMock;
import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import grammar.SymbolType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CoveringServiceTest extends AbstractServiceTest {

    private CoveringService coveringService;

    @BeforeEach
    public void initialize() {
        coveringService = new CoveringService();
    }

    @Test
    public void testAggressiveOperator() throws NoSuchFieldException, IllegalAccessException {
        Random randomMock = new RandomMock(Collections.singletonList(5));

        CoveringOperator operator = new AggressiveOperator();
        Field random = operator.getClass().getSuperclass().getDeclaredField("random");
        random.setAccessible(true);
        random.set(operator, randomMock);

        Field operatorField = coveringService.getClass().getDeclaredField("coveringOperator");
        operatorField.setAccessible(true);
        operatorField.set(coveringService, operator);

        TestGrammar simpleTestGrammar = new SimpleTestGrammar();
        Grammar grammar = simpleTestGrammar.getGrammar();

        Symbol A = new Symbol("A", 0, SymbolType.NON_TERMINAL);
        Symbol B = new Symbol("B", 0, SymbolType.NON_TERMINAL);
        Symbol E = new Symbol("E", 0, SymbolType.NON_TERMINAL);


        List<Rule> result = coveringService.run(grammar, B, A, false, false);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(E, result.get(0).getLeft());
        Assertions.assertEquals(B, result.get(0).getRight1());
        Assertions.assertEquals(A, result.get(0).getRight2());
    }

    @Test
    public void testProgressiveOperator() throws NoSuchFieldException, IllegalAccessException {
        Random randomMock = new RandomMock(Collections.singletonList(5));

        CoveringOperator operator = new ProgressiveOperator();
        Field random = operator.getClass().getSuperclass().getDeclaredField("random");
        random.setAccessible(true);
        random.set(operator, randomMock);

        Field operatorField = coveringService.getClass().getDeclaredField("coveringOperator");
        operatorField.setAccessible(true);
        operatorField.set(coveringService, operator);

        TestGrammar simpleTestGrammar = new SimpleTestGrammar();
        Grammar grammar = simpleTestGrammar.getGrammar();

        Symbol A = new Symbol("A", 0, SymbolType.NON_TERMINAL);
        Symbol B = new Symbol("B", 0, SymbolType.NON_TERMINAL);
        Symbol E = new Symbol("E", 0, SymbolType.NON_TERMINAL);


        List<Rule> result = coveringService.run(grammar, B, A, false, false);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(E, result.get(0).getLeft());
        Assertions.assertEquals(B, result.get(0).getRight1());
        Assertions.assertEquals(A, result.get(0).getRight2());
    }
}
