package configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class Configuration {

    private final static Logger LOGGER = LogManager.getLogger(Configuration.class);

    private static Configuration instance;

    private Properties properties;

    private final Map<String, String> overrides;

    private String name;

    private Configuration(String name, Properties properties) {
        this.overrides = new HashMap<>();
        this.properties = properties;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getString(String key) {
        return getProperty(key);
    }

    public Integer getInteger(String key) {
        String property = getProperty(key);
        if(property == null)
            return null;
        return Integer.parseInt(property);
    }

    public Double getDouble(String key) {
        String property = getProperty(key);
        if(property == null)
            return null;
        return Double.parseDouble(property);
    }

    public Boolean getBoolean(String key) {
        String property = getProperty(key);
        if(property == null)
            return null;
        return Boolean.parseBoolean(property);
    }

    public <T extends Enum> T getEnum(Function<String, T> finder, String key) {
        String property = getProperty(key);
        if(property == null)
            return null;
        return finder.apply(property);
    }

    void overrideProperty(String name, String value){
        overrides.put(name, value);
    }

    void resetProperties(){
        overrides.clear();
    }

    void replaceProperties(String name, Properties properties){
        this.name = name;
        this.properties = properties;
    }

    static void initialize(String name, Properties properties){
        if(instance != null)
            throw new IllegalStateException("Configuration already exists");
        instance = new Configuration(name, properties);
    }

    static Configuration getInstance() {
        if (instance == null)
            throw new IllegalStateException("Configuration not found");
        return instance;
    }

    static boolean exists() {
        return instance != null;
    }

    private String getProperty(String name) {
        return overrides.getOrDefault(name, properties.getProperty(name));
    }

}
