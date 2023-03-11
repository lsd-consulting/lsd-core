package com.lsd.events

import com.lsd.diagram.ValidComponentName
import com.lsd.report.model.DataHolder
import com.lsd.sanitiseMarkup

sealed interface SequenceMessage : DataHolder {
    val arrowType: ArrowType
    val from: String
    val to: String
    val colour: String

    override fun toMarkup(): String =
        """${ValidComponentName.of(from)} ${arrowType.toMarkup(colour)} ${ValidComponentName.of(to)}: <text fill="$colour">[[#${id} {${label.sanitiseMarkup()}} ${abbreviatedLabel}]]</text>"""
}

