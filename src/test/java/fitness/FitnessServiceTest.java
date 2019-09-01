package fitness;

import common.AbstractServiceTest;
import common.grammar.SimpleTestGrammar;
import common.grammar.TestGrammar;
import grammar.Grammar;
import grammar.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FitnessServiceTest extends AbstractServiceTest {

    private FitnessService fitnessService;

    @BeforeEach
    public void initialize() {
        fitnessService = FitnessService.getInstance();
    }

    @Test
    public void testCountFitness() {

        TestGrammar simpleTestGrammar = new SimpleTestGrammar();
        Grammar grammar = simpleTestGrammar.getGrammar();

        grammar.getNonTerminalRules().get(0).setCountUsageInNotValidSentencesParsing(0);
        grammar.getNonTerminalRules().get(0).setCountUsageInValidSentencesParsing(3);
        grammar.getNonTerminalRules().get(1).setCountUsageInNotValidSentencesParsing(4);
        grammar.getNonTerminalRules().get(1).setCountUsageInValidSentencesParsing(0);
        grammar.getNonTerminalRules().get(2).setCountUsageInNotValidSentencesParsing(3);
        grammar.getNonTerminalRules().get(2).setCountUsageInValidSentencesParsing(4);
        grammar.getNonTerminalRules().get(3).setCountUsageInNotValidSentencesParsing(0);
        grammar.getNonTerminalRules().get(3).setCountUsageInValidSentencesParsing(0);
        grammar.getNonTerminalRules().get(4).setCountUsageInNotValidSentencesParsing(1);
        grammar.getNonTerminalRules().get(4).setCountUsageInValidSentencesParsing(1);

        fitnessService.countRulesFitness(grammar);

        List<Rule> rules = grammar.getNonTerminalRules();
        Assertions.assertEquals(1., rules.get(0).getFitness());
        Assertions.assertEquals(0, rules.get(1).getFitness());
        Assertions.assertEquals(.5714285714285714, rules.get(2).getFitness());
        Assertions.assertEquals(0, rules.get(3).getFitness());
        Assertions.assertEquals(0.5, rules.get(4).getFitness());
    }

}
