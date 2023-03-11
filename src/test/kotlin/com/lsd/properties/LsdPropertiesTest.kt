package com.lsd.properties

import com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH
import com.lsd.properties.LsdProperties.OUTPUT_DIR
import com.lsd.properties.LsdProperties.getInt
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LsdPropertiesTest {

    @Test
    fun retrieveProperty() {
        assertThat(LsdProperties[LABEL_MAX_WIDTH]).isEqualTo("50")
    }

    @Test
    fun convertsIntegers() {
        assertThat(getInt(LABEL_MAX_WIDTH)).isEqualTo(50)
    }

    @Test
    fun containsDefaultOutputDir() {
        assertThat(LsdProperties[OUTPUT_DIR]).isEqualTo("build/reports/lsd")
    }
}