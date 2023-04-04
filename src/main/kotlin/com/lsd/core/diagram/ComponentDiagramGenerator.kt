package com.lsd.core.diagram

import com.github.jknack.handlebars.Handlebars
import com.lsd.core.IdGenerator
import com.lsd.core.adapter.puml.convertToSvg
import com.lsd.core.adapter.puml.toComponentMarkup
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType.*
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import com.lsd.core.domain.SequenceEvent
import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.core.report.model.Diagram

class ComponentDiagramGenerator(
    private val idGenerator: IdGenerator,
    private val events: List<SequenceEvent>,
    private val participants: List<Participant> = emptyList()
) {
    private val template = Handlebars().compile("templates/puml/component-uml")

    fun diagram(): Diagram? {
        if (events.isEmpty()) return null

        val uml = generateUml()
        return Diagram(
            id = idGenerator.next(),
            uml = uml,
            svg = convertToSvg(markup = uml)
        )
    }

    private fun generateUml(): String {
        val messages = events
            .filterIsInstance<Message>()
            .filter { it.type in listOf(SYNCHRONOUS, ASYNCHRONOUS, BI_DIRECTIONAL, LOST) }

        return template.apply(
            mapOf(
                "theme" to LsdProperties[DIAGRAM_THEME],
                "participants" to messages.mapToParticipants(providedParticipants = participants)
                    .map(Participant::toComponentMarkup)
                    .distinct(),
                "events" to messages
                    .map(Message::toComponentMarkup)
                    .distinct()
            )
        )
    }
}

fun List<Message>.mapToParticipants(providedParticipants: List<Participant>): List<Participant> {
    val participantsByName = providedParticipants.associateBy { it.componentName.name }
    return flatMap { listOf(it.from.name, it.to.name) }
        .distinct()
        .map { participantsByName.getOrElse(it) { PARTICIPANT.called(it) } }
}

