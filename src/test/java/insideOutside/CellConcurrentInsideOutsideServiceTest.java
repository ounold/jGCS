package insideOutside;

import configuration.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;

public class CellConcurrentInsideOutsideServiceTest extends InsideOutsideServiceTest {

    @BeforeEach
    public void setUp(){
        initGrammar();
        ConfigurationService.getInstance().overrideProperty("ce.outsideMode", "CELL_CONCURRENT");
    }

}
