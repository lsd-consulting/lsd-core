package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.LsdProperties.*;

public class DefaultProperties {

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LABEL_MAX_WIDTH, "200");
        properties.setProperty(DIAGRAM_THEME, "plain");
        properties.setProperty(OUTPUT_DIR, "build/reports/lsd");
        properties.setProperty(DETERMINISTIC_IDS, "false");
        properties.setProperty(MAX_EVENTS_PER_DIAGRAM, "50");
        return properties;
    }
}
