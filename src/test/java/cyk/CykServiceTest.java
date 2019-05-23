package cyk;

import common.AbstractServiceTest;
import common.testCase.*;
import configuration.ConfigurationService;
import org.junit.jupiter.api.Test;

class CykServiceTest extends AbstractServiceTest {

    private ConfigurationService configurationService = ConfigurationService.getInstance();
    private CykService cykService = CykService.getInstance();

    @Test
    public void simpleTestCase() {
        runTestCase(new SimpleGrammarTestCase(), false);
    }

    @Test
    public void stochasticTestCase() {
        runTestCase(new StochasticGrammarTestCase(), false);
    }

    @Test
    public void multipleStochasticTestCase() {
        runTestCase(new MutlipleStochasticTestCase(), false);
    }

    @Test
    public void multipleStochasticTestCaseWithInsides() {
        configurationService.overrideProperty("ce.calculateInsidesInCyk", "true");
        runTestCase(new MutlipleStochasticTestCaseWithInsides(), false);
    }

    @Test
    public void standardCoveringTestCase() {
        configurationService.overrideProperty("covering.operator", "STANDARD");
        runTestCase(new StandardCoveringGrammarTestCase(), true);
    }

    private void runTestCase(GrammarTestCase testCase, boolean enableCovering) {
        //when
        CykResult cykResult = cykService.runCyk(testCase.getSequence(), testCase.getGrammar(), enableCovering);

        //then
        testCase.assertResult(cykResult);
    }

}
