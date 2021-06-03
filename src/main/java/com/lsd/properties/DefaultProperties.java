package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.LsdProperties.*;
import static java.lang.System.getProperty;

public class DefaultProperties {

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LABEL_MAX_WIDTH, "200");
        properties.setProperty(DIAGRAM_THEME, "plain");
        properties.setProperty(OUTPUT_DIR, getProperty("java.io.tmpdir"));
        properties.setProperty(DETERMINISTIC_IDS, "false");
        return properties;
    }
}
