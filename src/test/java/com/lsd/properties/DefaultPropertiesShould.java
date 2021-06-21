package com.lsd.properties;

import org.junit.jupiter.api.Test;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultPropertiesShould {

    private final String property = randomAlphanumeric(30);

    @Test
    public void includeSystemProperties() {
        System.getProperties().put("testProperty", property);

        assertThat(DefaultProperties.defaultProperties().get("testProperty")).isEqualTo(property);
    }

    @Test
    public void overwriteHardCodedValuesWithSystemProperties() {
        System.getProperties().put(DIAGRAM_THEME, "CUSTOM");

        assertThat(DefaultProperties.defaultProperties().get(DIAGRAM_THEME)).isEqualTo("CUSTOM");
    }
}
