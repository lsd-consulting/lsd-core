package com.lsd.parse

import com.lsd.IdGenerator
import com.lsd.events.ArrowType
import com.lsd.events.SequenceEvent
import com.lsd.events.SynchronousResponse

class SynchronousResponseParser(private val idGenerator: IdGenerator) : Parser {

    private val regex = "^sync ?(.*?) from (.*?) to (.*?)(?: \\[#(.*)])?\$".toRegex()

    override fun parse(message: String, body: String): SequenceEvent? {
        return if (regex.containsMatchIn(message)) {
            val result = regex.find(message)!!
            val (label, from, to, colour) = result.destructured
            return SynchronousResponse(
                id = idGenerator.next(),
                from = from,
                to = to,
                colour = colour,
                arrowType = ArrowType.DOTTED_THIN,
                label = label,
                data = body
            )
        } else null
    }
}