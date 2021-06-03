package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.DefaultProperties.defaultProperties;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

/**
 * Convenience class to load up all the properties (defaults and user provided) at the start and make available globally
 */
public class LsdProperties {
    public static final String DIAGRAM_THEME = "lsd.core.diagram.theme";
    public static final String LABEL_MAX_WIDTH = "lsd.core.label.maxWidth";
    public static final String OUTPUT_DIR = "lsd.core.report.outputDir";
    public static final String DETERMINISTIC_IDS = "lsd.core.ids.deterministic";

    private static final Properties PROPERTIES = new PropertiesFileLoader("lsd.properties", defaultProperties()).load();

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static int getInt(String key) {
        return parseInt(PROPERTIES.getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return parseBoolean(PROPERTIES.getProperty(key));
    }
}
