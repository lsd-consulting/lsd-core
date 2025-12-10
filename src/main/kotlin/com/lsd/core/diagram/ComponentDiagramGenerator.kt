package com.lsd.core.diagram

import com.lsd.core.IdGenerator
import com.lsd.core.adapter.puml.convertToSvgAsync
import com.lsd.core.adapter.puml.toComponentMarkup
import com.lsd.core.builders.SequenceEventBuilder
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType.*
import com.lsd.core.domain.Participant
import com.lsd.core.domain.SequenceEvent
import com.lsd.core.report.HandlebarsWrapper
import com.lsd.core.report.model.Diagram

class ComponentDiagramGenerator(
    private val idGenerator: IdGenerator,
    private val events: Set<SequenceEvent>,
    private val participants: Set<Participant> = emptySet()
) {
    constructor(
        idGenerator: IdGenerator,
        eventBuilders: List<SequenceEventBuilder>,
        participants: Set<Participant> = emptySet()
    ) : this(idGenerator, eventBuilders.map(SequenceEventBuilder::build).toSet(), participants)

    private val template = HandlebarsWrapper.compile("templates/puml/component-uml")

    fun diagram(): Diagram? {
        if (events.isEmpty()) return null

        val uml = generateUml()
        return Diagram(
            id = idGenerator.next(),
            uml = uml,
            svg = convertToSvgAsync(markup = uml)
        )
    }

    private fun generateUml(): String {
        val messages = events
            .filterIsInstance<Message>()
            .filter { it.type in listOf(SYNCHRONOUS, ASYNCHRONOUS, BI_DIRECTIONAL, LOST) }
            .distinctBy { it.from to it.to }

        return template.apply(
            mapOf(
                "theme" to "plain",
                "participants" to participants.usedIn(messages)
                    .map(Participant::toComponentMarkup),
                "events" to messages.map(Message::toComponentMarkup)
            )
        )
    }
}
