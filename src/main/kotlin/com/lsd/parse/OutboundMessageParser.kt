package com.lsd.parse

import com.lsd.IdGenerator
import com.lsd.events.ArrowType.SOLID
import com.lsd.events.Message
import com.lsd.events.SequenceEvent

class OutboundMessageParser(private val idGenerator: IdGenerator) : Parser {

    private val regex = "^(?!sync)(.*?) from (.*?) to (.*?)(?: \\[#(.*)])?$".toRegex()

    override fun parse(message: String, body: String): SequenceEvent? {
        return if (regex.containsMatchIn(message)) {
            val (label, from, to, colour) = regex.find(message)!!.destructured
            return Message(
                id = idGenerator.next(),
                from = from,
                to = to,
                data = body,
                label = label,
                colour = colour,
                arrowType = SOLID
            )
        } else null
    }
}