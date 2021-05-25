package com.lsd.properties;

import java.util.Properties;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;

public class DefaultProperties {

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LABEL_MAX_WIDTH, "200");
        properties.setProperty(DIAGRAM_THEME, "plain");
        return properties;
    }
}
