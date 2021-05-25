package com.lsd.properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;
import static org.assertj.core.api.Assertions.assertThat;

class PropertiesFileLoaderTest {
    private static Properties defaultProperties = new Properties();

    @BeforeAll
    public static void loadDefaultProperties() {
        defaultProperties.setProperty(LABEL_MAX_WIDTH, "30");
        defaultProperties.setProperty("some-property-not-in-file", "ABC");
    }

    @Test
    void loadPropertiesFromFile() {
        PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader("lsd.properties", defaultProperties);

        Properties loadedProperties = propertiesFileLoader.load();

        assertThat(loadedProperties.getProperty(LABEL_MAX_WIDTH)).isEqualTo("50");
    }

    @Test
    void fallBackToDefaultValues() {
        PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader("wrong-name.properties", defaultProperties);

        Properties loadedProperties = propertiesFileLoader.load();

        assertThat(loadedProperties.getProperty("some-property-not-in-file"))
                .isNotEmpty()
                .isEqualTo(defaultProperties.get("some-property-not-in-file"));
    }

    @Test
    void handleInvalidPropertyFilesAndFallBackToDefaults() {
        PropertiesFileLoader propertiesFileLoader = new PropertiesFileLoader("dodgy.properties", defaultProperties);

        Properties loadedProperties = propertiesFileLoader.load();

        assertThat(loadedProperties.getProperty(LABEL_MAX_WIDTH))
                .isNotEmpty()
                .isEqualTo(defaultProperties.get(LABEL_MAX_WIDTH));
    }
}