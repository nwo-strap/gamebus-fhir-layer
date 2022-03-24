package nwo.strap.fhir;

import java.io.IOException;
import java.util.Properties;

/**
 * This class provides methods to get property from `.properties` files,
 * including `config.properties` and `token.properties`.
 */
public class Config {

    private Config() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Properties CONFIGS = new Properties();
    private static final Properties TOKENS = new Properties();

    static {
        // Load configs
        try {
            CONFIGS.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        // Load tokens
        try {
            TOKENS.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("token.properties"));
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

    /**
     * Get GameBus player ID.
     * The IDs are provided in `token.properties` file.
     *
     * @param token GameBus Bearer token
     * @return GameBus player ID
     */
    public static String getId(String token) {
        return TOKENS.getProperty(token);
    }

    /**
     * Test if the specified token is contained in the configuration.
     * <p>
     * The GameBus Bearer tokens are configured in the `token.properties` file.
     *
     * @param token GameBus Bearer token to be validated
     * @return boolean
     */
    public static boolean containsToken(String token) {
        return TOKENS.containsKey(token);
    }
}
