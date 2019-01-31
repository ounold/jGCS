package insideOutside;

import configuration.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;

public class CellRuleConcurrentInsideOutsideServiceTest extends InsideOutsideServiceTest {

    @BeforeEach
    public void setUp(){
        initGrammar();
        ConfigurationService.getInstance().overrideProperty("ce.outsideMode", "CELL_RULE_CONCURRENT");
    }

}
