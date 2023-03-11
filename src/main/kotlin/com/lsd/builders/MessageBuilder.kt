package com.lsd.builders

import com.lsd.events.ArrowType
import com.lsd.events.Message

class MessageBuilder {
    private var instance = Message(id = "", from = "", to = "", label = "")

    fun id(id: String): MessageBuilder = also { instance = instance.copy(id = id) }
    fun from(from: String): MessageBuilder = also { instance = instance.copy(from = from) }
    fun to(to: String): MessageBuilder = also { instance = instance.copy(to = to) }
    fun label(label: String): MessageBuilder = also { instance = instance.copy(label = label) }
    fun data(data: Any): MessageBuilder = also { instance = instance.copy(data = data) }
    fun colour(colour: String): MessageBuilder = also { instance = instance.copy(colour = colour) }
    fun arrowType(arrowType: ArrowType): MessageBuilder =
        also { instance = instance.copy(arrowType = arrowType) }

    fun build(): Message = instance
}