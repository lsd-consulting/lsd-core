package com.lsd.report.model

import com.github.jknack.handlebars.internal.lang3.StringUtils
import com.lsd.events.SequenceEvent
import com.lsd.properties.LsdProperties
import com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH

/**
 * This type of event holds extra data in addition to what is displayed on the diagram and therefore
 * we can use popups etc. to display this data.
 */
interface DataHolder : SequenceEvent {
    val id: String
    val label: String
    val data: Any?

    /**
     * @return An abbreviation of the label if the length exceeds the configured maximum width.
     */
    val abbreviatedLabel: String
        get() = StringUtils.abbreviate(label.trim(), "...", LsdProperties.getInt(LABEL_MAX_WIDTH))
}