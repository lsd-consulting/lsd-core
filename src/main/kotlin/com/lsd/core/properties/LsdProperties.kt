package com.lsd.core.properties

/**
 * Convenience class to load up all the properties (defaults and user provided) at the start and make available globally
 */
object LsdProperties {
    const val DIAGRAM_THEME = "lsd.core.diagram.theme"
    const val LABEL_MAX_WIDTH = "lsd.core.label.maxWidth"
    const val OUTPUT_DIR = "lsd.core.report.outputDir"
    const val DETERMINISTIC_IDS = "lsd.core.ids.deterministic"
    const val MAX_EVENTS_PER_DIAGRAM = "lsd.core.diagram.sequence.maxEventsPerDiagram"
    const val DEV_MODE = "lsd.core.devMode"
    const val METRICS_ENABLED = "lsd.core.metrics.enabled"

    private val PROPERTIES = PropertiesLoader("lsd.properties", DefaultProperties.defaultProperties()).load()

    @JvmStatic
    operator fun get(key: String): String {
        return PROPERTIES.getProperty(key)
    }

    @JvmStatic
    operator fun get(key: String, default: String): String {
        return PROPERTIES.getProperty(key, default)
    }

    @JvmStatic
    fun getInt(key: String): Int {
        return PROPERTIES.getProperty(key).toInt()
    }

    @JvmStatic
    fun getInt(key: String, default: Int): Int {
        return PROPERTIES.getProperty(key, default.toString()).toInt()
    }

    @JvmStatic
    fun getLong(key: String): Long {
        return PROPERTIES.getProperty(key).toLong()
    }

    @JvmStatic
    fun getLong(key: String, default: Long): Long {
        return PROPERTIES.getProperty(key, default.toString()).toLong()
    }

    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return PROPERTIES.getProperty(key).toBoolean()
    }

    @JvmStatic
    fun getBoolean(key: String, default: Boolean): Boolean {
        return PROPERTIES.getProperty(key, default.toString()).toBoolean()
    }
}