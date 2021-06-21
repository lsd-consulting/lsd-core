package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.LsdProperties.*;

public class DefaultProperties {

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LABEL_MAX_WIDTH, "200");
        properties.setProperty(DIAGRAM_THEME, "plain");
        properties.setProperty(DETERMINISTIC_IDS, "false");
        properties.putAll(System.getProperties());
        return properties;
    }
}
