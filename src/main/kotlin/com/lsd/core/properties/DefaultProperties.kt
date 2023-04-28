package com.lsd.core.properties

import com.lsd.core.properties.LsdProperties.DETERMINISTIC_IDS
import com.lsd.core.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.core.properties.LsdProperties.LABEL_MAX_WIDTH
import com.lsd.core.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.core.properties.LsdProperties.OUTPUT_DIR
import java.util.*

object DefaultProperties {
    fun defaultProperties(): Properties {
        return Properties().apply {
            setProperty(LABEL_MAX_WIDTH, "200")
            setProperty(DIAGRAM_THEME, "lsd-light from https://cdn.jsdelivr.net/gh/lsd-consulting/lsd-core@4/src/main/resources/static/")
            setProperty(OUTPUT_DIR, "build/reports/lsd")
            setProperty(DETERMINISTIC_IDS, "false")
            setProperty(MAX_EVENTS_PER_DIAGRAM, "50")
        }
    }
}