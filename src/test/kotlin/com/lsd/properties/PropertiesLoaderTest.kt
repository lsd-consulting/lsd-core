package com.lsd.properties

import com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetSystemProperty
import java.util.*

internal class PropertiesLoaderTest {

    private val defaultProperties = defaultProperties(mapOf(LABEL_MAX_WIDTH to "30"))
    private val propertiesLoader = PropertiesLoader("lsd.properties", defaultProperties)

    @Test
    fun loadDefaultPropertiesIfFileIsMissing() {
        val properties = PropertiesLoader("wrong-name.properties", defaultProperties).load()
        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo(defaultProperties[LABEL_MAX_WIDTH])
    }

    @Test
    fun filePropertyOverridesDefaultProperty() {
        val properties = propertiesLoader.load()
        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo("50")
    }

    @Test
    @SetSystemProperty(key = LABEL_MAX_WIDTH, value = "789")
    fun systemPropertyOverridesFileProperty() {
        val properties = propertiesLoader.load()
        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo("789")
    }

    @Test
    fun gracefullyHandleInvalidPropertyFile() {
        val propertiesLoader = PropertiesLoader("dodgy.properties", defaultProperties)
        val properties = propertiesLoader.load()
        assertThat(properties.getProperty(LABEL_MAX_WIDTH)).isEqualTo(defaultProperties[LABEL_MAX_WIDTH])
    }

    private fun defaultProperties(properties: Map<String?, String?>): Properties {
        val defaults = Properties()
        defaults.putAll(properties)
        return defaults
    }
}