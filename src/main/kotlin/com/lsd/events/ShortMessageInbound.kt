package com.lsd.events

import com.lsd.builders.ShortMessageInboundBuilder
import com.lsd.diagram.ValidComponentName
import com.lsd.events.ArrowType.SOLID
import com.lsd.sanitiseMarkup

data class ShortMessageInbound(
    override val id: String,
    override val to: String,
    override val label: String,
    override val data: Any = "",
    override val colour: String = "",
    override val arrowType: ArrowType = SOLID,
    override val from: String = ""
) : SequenceMessage {

    override fun toMarkup(): String {
        val tooltip = label.sanitiseMarkup()
        val arrow = arrowType.toMarkup(colour)
        return """?$arrow ${ValidComponentName.of(to)}: <text fill="$colour">[[#${id} {$tooltip} ${abbreviatedLabel}]]</text>"""
    }

    companion object {
        @JvmStatic
        fun builder(): ShortMessageInboundBuilder = ShortMessageInboundBuilder()
    }
}