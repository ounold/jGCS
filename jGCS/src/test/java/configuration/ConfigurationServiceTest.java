package configuration;

import common.AbstractServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigurationServiceTest extends AbstractServiceTest {

    private ConfigurationService configurationService = ConfigurationService.getInstance();

    @Test
    public void testLoadConfiguration(){
        configurationService.loadConfigurationFile("src/test/resources/test.properties");
        Configuration configuration = configurationService.getConfiguration();
        Assertions.assertEquals("probability", configuration.getString("c.string"));
        Assertions.assertEquals(13, configuration.getInteger("b.int").intValue());
        Assertions.assertEquals(.07, configuration.getDouble("a.double").doubleValue());
    }

    @Test
    public void testOverrideProperty(){
        configurationService.loadConfigurationFile("src/test/resources/test.properties");
        configurationService.overrideProperty("b.int", "test");
        Assertions.assertEquals("test", ConfigurationService.getConfiguration().getString("b.int"));
    }

    @Test
    public void testResetProperties(){
        configurationService.loadConfigurationFile("src/test/resources/test.properties");
        configurationService.overrideProperty("b.int", "test");
        configurationService.resetProperties();
        Assertions.assertEquals(13, ConfigurationService.getConfiguration().getInteger("b.int").intValue());
    }

}