package com.lsd.properties

import com.lsd.properties.LsdProperties.DETERMINISTIC_IDS
import com.lsd.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH
import com.lsd.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM
import com.lsd.properties.LsdProperties.OUTPUT_DIR
import java.util.*

object DefaultProperties {
    fun defaultProperties(): Properties {
        return Properties().apply {
            setProperty(LABEL_MAX_WIDTH, "200")
            setProperty(DIAGRAM_THEME, "plain")
            setProperty(OUTPUT_DIR, "build/reports/lsd")
            setProperty(DETERMINISTIC_IDS, "false")
            setProperty(MAX_EVENTS_PER_DIAGRAM, "50")
        }
    }
}