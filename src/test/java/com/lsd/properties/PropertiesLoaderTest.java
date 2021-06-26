package com.lsd.properties;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.util.Map;
import java.util.Properties;

import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;
import static org.assertj.core.api.Assertions.assertThat;

class PropertiesLoaderTest {

    private final Properties defaultProperties = defaultProperties(Map.of(
            LABEL_MAX_WIDTH, "30"
    ));

    private final PropertiesLoader propertiesLoader = new PropertiesLoader("lsd.properties", defaultProperties);

    @Test
    void loadDefaultPropertiesIfFileIsMissing() {
        var propertiesLoader = new PropertiesLoader("wrong-name.properties", defaultProperties);

        var properties = propertiesLoader.load();

        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo(defaultProperties.get(LABEL_MAX_WIDTH));
    }

    @Test
    void filePropertyOverridesDefaultProperty() {
        var properties = propertiesLoader.load();

        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo("50");
    }

    @Test
    @SetSystemProperty(key = LABEL_MAX_WIDTH, value = "789")
    void systemPropertyOverridesFileProperty() {
        var properties = propertiesLoader.load();

        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo("789");
    }

    @Test
    void gracefullyHandleInvalidPropertyFile() {
        var propertiesLoader = new PropertiesLoader("dodgy.properties", defaultProperties);

        var properties = propertiesLoader.load();

        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo(defaultProperties.get(LABEL_MAX_WIDTH));
    }

    private Properties defaultProperties(Map<String, String> properties) {
        var defaults = new Properties();
        defaults.putAll(properties);
        return defaults;
    }
}