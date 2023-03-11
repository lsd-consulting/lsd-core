package com.lsd.builders

import com.lsd.events.ArrowType
import com.lsd.events.ShortMessageInbound

class ShortMessageInboundBuilder {
    private var instance = ShortMessageInbound(id = "", to = "", label = "")

    fun id(id: String): ShortMessageInboundBuilder =
        also { instance = instance.copy(id = id) }

    fun to(to: String): ShortMessageInboundBuilder =
        also { instance = instance.copy(to = to) }

    fun label(label: String): ShortMessageInboundBuilder =
        also { instance = instance.copy(label = label) }

    fun data(data: Any): ShortMessageInboundBuilder =
        also { instance = instance.copy(data = data) }

    fun colour(colour: String): ShortMessageInboundBuilder =
        also { instance = instance.copy(colour = colour) }

    fun arrowType(arrowType: ArrowType): ShortMessageInboundBuilder =
        also { instance = instance.copy(arrowType = arrowType) }

    fun build(): ShortMessageInbound = instance
}