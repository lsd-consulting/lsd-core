package com.lsd.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Slf4j
public class PropertiesLoader {

    private final String fileName;
    private final Properties defaults;

    public PropertiesLoader(String fileName, Properties defaults) {
        this.fileName = fileName;
        this.defaults = defaults;
    }

    public Properties load() {
        var inputStream = getClass().getResourceAsStream("/" + fileName);
        var properties = new Properties(defaults);
        ofNullable(inputStream).ifPresent(loadInto(properties));
        properties.putAll(System.getProperties());

        return properties;
    }

    private Consumer<InputStream> loadInto(Properties properties) {
        return stream -> {
            try {
                properties.load(stream);
            } catch (IOException e) {
                log.warn("Failed to load properties file. Default values will be used.", e);
            }
        };
    }
}
