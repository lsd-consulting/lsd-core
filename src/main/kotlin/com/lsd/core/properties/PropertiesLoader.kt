package com.lsd.core.properties

import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.function.Consumer

class PropertiesLoader(
    private val fileName: String,
    private val defaults: Properties
) {
    fun load(): Properties {
        val inputStream = javaClass.getResourceAsStream("/$fileName")
        val properties = Properties(defaults)
        Optional.ofNullable(inputStream).ifPresent(loadInto(properties))
        properties.putAll(System.getProperties())
        return properties
    }

    private fun loadInto(properties: Properties): Consumer<InputStream> {
        return Consumer { stream: InputStream ->
            try {
                properties.load(stream)
            } catch (e: IOException) {
                println("Failed to load properties file, falling back to default values. Error: [${e.message}]")
            }
        }
    }
}