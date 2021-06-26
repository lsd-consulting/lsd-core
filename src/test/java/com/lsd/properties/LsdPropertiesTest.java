package com.lsd.properties;

import org.junit.jupiter.api.Test;

import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;
import static com.lsd.properties.LsdProperties.OUTPUT_DIR;
import static org.assertj.core.api.Assertions.assertThat;

class LsdPropertiesTest {

    @Test
    void retrieveProperty() {
        assertThat(LsdProperties.get(LABEL_MAX_WIDTH)).isEqualTo("50");
    }

    @Test
    void convertsIntegers() {
        assertThat(LsdProperties.getInt(LABEL_MAX_WIDTH)).isEqualTo(50);
    }

    @Test
    void containsDefaultOutputDir() {
        assertThat(LsdProperties.get(OUTPUT_DIR)).isEqualTo("build/reports/lsd");
    }
}