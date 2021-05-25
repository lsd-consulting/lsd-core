package com.lsd.properties;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Slf4j
public class PropertiesFileLoader {

    private final String fileName;
    private final Properties defaults;

    public PropertiesFileLoader(String fileName, Properties defaults) {
        this.fileName = fileName;
        this.defaults = defaults;
    }

    public Properties loadFromFile() {
        InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
        Properties properties = new Properties(defaults);
        ofNullable(inputStream).ifPresent(loadInto(properties));

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
