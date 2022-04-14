package nwo.strap.fhir;

import java.io.IOException;
import java.util.Properties;

/**
 * This class provides methods to get property from `config.properties` file
 */
public class Config {

    private Config() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Properties CONFIGS = new Properties();

    static {
        // Load configs
        try {
            CONFIGS.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Get configuration value.
     * <p>
     * The configurations are provided in `config.properties` file.
     *
     * @param key configuration key
     * @return configuration value
     */
    public static String getConfig(String key) {
        return CONFIGS.getProperty(key);
    }
}