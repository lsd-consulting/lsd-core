package com.lsd.events

import com.lsd.builders.ShortMessageOutboundBuilder
import com.lsd.diagram.ValidComponentName
import com.lsd.events.ArrowType.SOLID
import com.lsd.sanitiseMarkup

data class ShortMessageOutbound(
    override val id: String,
    override val label: String,
    override val data: Any = "",
    override val from: String,
    override val colour: String = "",
    override val arrowType: ArrowType = SOLID,
    override val to: String = ""
) : SequenceMessage {

    override fun toMarkup(): String {
        val tooltip = label.sanitiseMarkup()
        return """${ValidComponentName.of(from)} ${arrowType.toMarkup(colour)}?: <text fill="$colour">[[#${id} {$tooltip} ${abbreviatedLabel}]]</text>"""
    }

    companion object {
        @JvmStatic
        fun builder(): ShortMessageOutboundBuilder = ShortMessageOutboundBuilder()
    }
}