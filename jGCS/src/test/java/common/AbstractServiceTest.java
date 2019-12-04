package common;

import configuration.ConfigurationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractServiceTest {

    @BeforeAll
    public static void init(){
        ConfigurationService.getInstance().loadConfigurationFile("src/test/resources/dymon.properties");
    }

    @AfterAll
    public static void cleanUp(){
        ConfigurationService.getInstance().resetProperties();
    }

}
