package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationService {

    public static final String DEFAULT_PATH = "default.properties";

    private static ConfigurationService instance;

    private ConfigurationService() {
    }

    public static ConfigurationService getInstance() {
        if (instance == null)
            instance = new ConfigurationService();
        return instance;
    }

    public static Configuration getConfiguration() {
        return Configuration.getInstance();
    }

    public void loadConfigurationFile(String path) {
        Properties properties = new Properties();
        String name = "";
        try {
            File propertiesFile = new File(path);
            Matcher nameMatcher = Pattern.compile("(.*)\\.properties")
                    .matcher(propertiesFile.getName());
            if (nameMatcher.matches())
                name = nameMatcher.group(1);
            else
                name = "";
            InputStream input = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Configuration could not be loaded", e);
        }
        if (Configuration.exists()) {
            Configuration configuration = Configuration.getInstance();
            configuration.replaceProperties(name, properties);
        } else {
            Configuration.initialize(name, properties);
        }
    }

    public void overrideProperty(String name, String value) {
        getConfiguration().overrideProperty(name, value);
    }

    public void resetProperties() {
        getConfiguration().resetProperties();
    }
}
