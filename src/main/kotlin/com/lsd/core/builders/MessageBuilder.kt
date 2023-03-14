package com.lsd.core.builders

import com.lsd.core.domain.ComponentName
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.Participant

class MessageBuilder {
    private var message = Message(id = "", from = ComponentName(""), to = ComponentName(""), label = "")

    fun id(id: String) = also { message = message.copy(id = id) }
    fun from(from: String) = also { message = message.copy(from = ComponentName(from)) }
    fun from(from: Participant) = also { message = message.copy(from = from.component) }
    fun to(to: String) = also { message = message.copy(to = ComponentName(to)) }
    fun to(to: Participant) = also { message = message.copy(to = to.component) }
    fun label(label: String) = also { message = message.copy(label = label) }
    fun data(data: Any) = also { message = message.copy(data = data) }
    fun colour(colour: String) = also { message = message.copy(colour = colour) }
    fun type(type: MessageType) = also { message = message.copy(type = type) }

    fun build(): Message = message
    
    companion object {
        @JvmStatic
        fun messageBuilder(): MessageBuilder = MessageBuilder()
    }
}
