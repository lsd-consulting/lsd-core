package com.lsd.core

import com.lsd.core.properties.LsdProperties.DEV_MODE
import com.lsd.core.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.core.properties.LsdProperties.METRICS_ENABLED
import com.lsd.core.properties.LsdProperties.getBoolean
import com.lsd.core.properties.LsdProperties.getInt

data class ReportOptions(
    val devMode: Boolean = getBoolean(DEV_MODE),
    val metricsEnabled: Boolean = getBoolean(METRICS_ENABLED),
    val maxEventsPerDiagram: Int = getInt(MAX_EVENTS_PER_DIAGRAM),
)

/**
 * Convenience builder for Java projects
 */
class ReportOptionsBuilder() {
    private var options = ReportOptions()

    fun metricsEnabled(isEnabled: Boolean): ReportOptionsBuilder = also {
        options = options.copy(metricsEnabled = isEnabled)
    }

    fun devMode(isEnabled: Boolean): ReportOptionsBuilder = also {
        options = options.copy(devMode = isEnabled)
    }

    fun maxEventsPerDiagram(max: Int): ReportOptionsBuilder = also {
        options = options.copy(maxEventsPerDiagram = max)
    }

    fun build(): ReportOptions = options
}
