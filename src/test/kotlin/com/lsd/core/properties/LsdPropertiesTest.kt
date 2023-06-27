package com.lsd.core.properties

import com.lsd.core.properties.LsdProperties.DEV_MODE
import com.lsd.core.properties.LsdProperties.METRICS_ENABLED
import com.lsd.core.properties.LsdProperties.LABEL_MAX_WIDTH
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LsdPropertiesTest {

    @Test
    fun retrieveProperty() {
        assertThat(LsdProperties[LABEL_MAX_WIDTH]).isEqualTo("50")
    }

    @Test
    fun convertsIntegers() {
        assertThat(LsdProperties.getInt(LABEL_MAX_WIDTH)).isEqualTo(50)
    }
    
    @Test
    fun convertsBooleans() {
        assertThat(LsdProperties.getBoolean(DEV_MODE)).isEqualTo(true)
        assertThat(LsdProperties.getBoolean(METRICS_ENABLED)).isEqualTo(true)
    }

    @Test
    fun containsDefaultOutputDir() {
        assertThat(LsdProperties[OUTPUT_DIR]).isEqualTo("build/reports/lsd")
    }
}