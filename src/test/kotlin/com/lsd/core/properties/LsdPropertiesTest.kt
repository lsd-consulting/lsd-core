package com.lsd.core.properties

import com.lsd.core.properties.LsdProperties.LABEL_MAX_WIDTH
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import com.lsd.core.properties.LsdProperties.USE_LOCAL_STATIC_FILES
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
    fun containsDefaultOutputDir() {
        assertThat(LsdProperties[OUTPUT_DIR]).isEqualTo("build/reports/lsd")
    }
    
    @Test
    fun containsDefaultUseLocalStaticFiles() {
        assertThat(LsdProperties[USE_LOCAL_STATIC_FILES]).isEqualTo("true")
    }
}