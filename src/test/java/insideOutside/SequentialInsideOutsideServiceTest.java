package insideOutside;

import configuration.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;

public class SequentialInsideOutsideServiceTest extends InsideOutsideServiceTest {

    @BeforeEach
    public void setUp(){
        initGrammar();
        ConfigurationService.getInstance().overrideProperty("ce.outsideMode", "SEQUENTIAL");
    }

}
