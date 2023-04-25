package com.lsd.core.adapter.parse

import com.lsd.core.IdGenerator
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import com.lsd.core.domain.SequenceEvent

@Deprecated(message = "To be deleted")
class SynchronousMessageParser(private val idGenerator: IdGenerator) : Parser {

    private val regex = "^(?!sync)(.*?) from (.*?) to (.*?)(?: \\[#(.*)])?$".toRegex()

    override fun parse(message: String, body: String): SequenceEvent? {
        return if (regex.containsMatchIn(message)) {
            val (label, from, to, colour) = regex.find(message)!!.destructured
            return Message(
                id = idGenerator.next(),
                from = PARTICIPANT.called(from),
                to = PARTICIPANT.called(to),
                data = body,
                label = label,
                colour = colour,
                type = MessageType.SYNCHRONOUS
            )
        } else null
    }
}