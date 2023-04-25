package com.lsd.core.adapter.parse

import com.lsd.core.IdGenerator
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import com.lsd.core.domain.SequenceEvent

@Deprecated(message = "To be deleted")
class SynchronousResponseParser(private val idGenerator: IdGenerator) : Parser {

    private val regex = "^sync ?(.*?) from (.*?) to (.*?)(?: \\[#(.*)])?\$".toRegex()

    override fun parse(message: String, body: String): SequenceEvent? {
        return if (regex.containsMatchIn(message)) {
            val result = regex.find(message)!!
            val (label, from, to, colour) = result.destructured
            return Message(
                id = idGenerator.next(),
                from = PARTICIPANT.called(from),
                to = PARTICIPANT.called(to),
                colour = colour,
                type = SYNCHRONOUS_RESPONSE,
                label = label,
                data = body
            )
        } else null
    }
}