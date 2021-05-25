package com.lsd.properties;

import org.junit.jupiter.api.Test;

import static com.lsd.properties.DefaultProperties.LABEL_MAX_WIDTH;
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
}