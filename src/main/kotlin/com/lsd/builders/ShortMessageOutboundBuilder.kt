package com.lsd.builders

import com.lsd.events.ArrowType
import com.lsd.events.ShortMessageOutbound

class ShortMessageOutboundBuilder {
    private var instance = ShortMessageOutbound(id = "", from = "", label = "")

    fun id(id: String): ShortMessageOutboundBuilder =
        also { instance = instance.copy(id = id) }

    fun from(from: String): ShortMessageOutboundBuilder =
        also { instance = instance.copy(from = from) }

    fun label(label: String): ShortMessageOutboundBuilder =
        also { instance = instance.copy(label = label) }

    fun data(data: Any): ShortMessageOutboundBuilder =
        also { instance = instance.copy(data = data) }

    fun colour(colour: String): ShortMessageOutboundBuilder =
        also { instance = instance.copy(colour = colour) }

    fun arrowType(arrowType: ArrowType): ShortMessageOutboundBuilder =
        also { instance = instance.copy(arrowType = arrowType) }

    fun build(): ShortMessageOutbound = instance
}