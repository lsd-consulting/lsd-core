package com.lsd.core.properties

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class LsdPropertiesTest {

    @Test
    fun retrieveProperty() {
        Assertions.assertThat(LsdProperties[LsdProperties.LABEL_MAX_WIDTH]).isEqualTo("50")
    }

    @Test
    fun convertsIntegers() {
        Assertions.assertThat(LsdProperties.getInt(LsdProperties.LABEL_MAX_WIDTH)).isEqualTo(50)
    }

    @Test
    fun containsDefaultOutputDir() {
        Assertions.assertThat(LsdProperties[LsdProperties.OUTPUT_DIR]).isEqualTo("build/reports/lsd")
    }
}