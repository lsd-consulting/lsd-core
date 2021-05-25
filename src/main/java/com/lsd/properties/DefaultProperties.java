package com.lsd.properties;

import java.util.Properties;

public class DefaultProperties {

    public static final String LABEL_MAX_WIDTH = "lsd.core.label.max-width";

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(LABEL_MAX_WIDTH, "200");
        return properties;
    }
}
