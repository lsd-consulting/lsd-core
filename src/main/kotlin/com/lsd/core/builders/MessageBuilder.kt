package com.lsd.core.builders

import com.lsd.core.domain.ComponentName
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.Participant

class MessageBuilder {
    private var message = Message(id = "", from = ComponentName(""), to = ComponentName(""), label = "")

    fun id(id: String) = also { message = message.copy(id = id) }
    fun from(from: String) = also { from(ComponentName(from)) }
    fun from(from: Participant) = also { from(from.componentName) }
    fun from(from: ComponentName) = also { message = message.copy(from = from) }
    fun to(to: String) = also { to(ComponentName(to)) }
    fun to(to: Participant) = also { to(to.componentName) }
    fun to(to: ComponentName) = also { message = message.copy(to = to) }
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
