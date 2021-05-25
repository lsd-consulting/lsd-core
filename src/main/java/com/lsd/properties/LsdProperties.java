package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.DefaultProperties.defaultProperties;
import static java.lang.Integer.parseInt;

/**
 * Convenience class to load up all the properties (defaults and user provided) at the start and make available globally
 */
public class LsdProperties {
    private static final String PROPERTIES_FILE_NAME = "lsd.properties";
    private static final Properties PROPERTIES = new PropertiesFileLoader(PROPERTIES_FILE_NAME, defaultProperties()).loadFromFile();

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key) {
        return parseInt(PROPERTIES.getProperty(key));
    }
}
