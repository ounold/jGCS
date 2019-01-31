package common.util;

import application.ApplicationException;
import grammar.Grammar;
import grammar.Rule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

public final class TestUtils {

    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);


    private TestUtils() {
    }

    public static void assertEquals(Grammar expected, Grammar actual, double delta) {
        try {
            actual.getRules().forEach(ruleB -> expected.getRule(ruleB.getDefinition()));
        } catch (ApplicationException exc) {
            throw new AssertionFailedError("Expected grammar throws: " + exc.getMessage());
        }
        try {
            expected.getRules().forEach(ruleA -> {
                Rule ruleB = actual.getRule(ruleA.getDefinition());
                LOGGER.debug("Comparing rule: {}", ruleA.getDefinition());
                Assertions.assertEquals(ruleA.getProbability(), ruleB.getProbability(), delta, ruleA.getDefinition());
            });
        } catch (ApplicationException exc) {
            throw new AssertionFailedError("Actual grammar throws: " + exc.getMessage());
        }
    }

    public static void logValues(Grammar expected, Grammar actual) {
        try {
            actual.getRules().forEach(ruleB -> expected.getRule(ruleB.getDefinition()));
        } catch (ApplicationException exc) {
            throw new AssertionFailedError("Expected grammar throws: " + exc.getMessage());
        }
        try {
            expected.getRules().forEach(ruleA -> {
                Rule ruleB = actual.getRule(ruleA.getDefinition());
                LOGGER.debug("Comparing rule {}. Expected: {}. Actual: {}.", ruleA.getDefinition(), ruleA.getProbability(), ruleB.getProbability());
            });
        } catch (ApplicationException exc) {
            throw new AssertionFailedError("Actual grammar throws: " + exc.getMessage());
        }
    }

}
