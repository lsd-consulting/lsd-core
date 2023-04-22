package com.lsd.core.builders

import com.lsd.core.IdGenerator
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.PARTICIPANT

private val idGenerator = IdGenerator(isDeterministic = false)

class MessageBuilder {
    private var message =
        Message(id = idGenerator.next(), from = PARTICIPANT.called(""), to = PARTICIPANT.called(""), label = "")

    fun id(id: String) = also { message = message.copy(id = id) }
    fun from(from: String) = also { message = message.copy(from = PARTICIPANT.called(from)) }
    fun from(from: Participant) = also { message = message.copy(from = from) }
    fun to(to: String) = also { message = message.copy(to = PARTICIPANT.called(to)) }
    fun to(to: Participant) = also { message = message.copy(to = to) }
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
