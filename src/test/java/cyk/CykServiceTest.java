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
        runTestCase(new SimpleGrammarTestCase());
    }

    @Test
    public void stochasticTestCase() {
        runTestCase(new StochasticGrammarTestCase());
    }

    @Test
    public void multipleStochasticTestCase() {
        runTestCase(new MutlipleStochasticTestCase());
    }

    @Test
    public void multipleStochasticTestCaseWithInsides() {
        configurationService.overrideProperty("ce.calculateInsidesInCyk", "true");
        runTestCase(new MutlipleStochasticTestCaseWithInsides());
    }

    private void runTestCase(GrammarTestCase testCase) {
        //when
        CykResult cykResult = cykService.runCyk(testCase.getSequence(), testCase.getGrammar());

        //then
        testCase.assertResult(cykResult);
    }

}