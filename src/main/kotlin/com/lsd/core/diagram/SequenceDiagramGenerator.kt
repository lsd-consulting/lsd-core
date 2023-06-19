package com.lsd.core.diagram

import com.github.jknack.handlebars.Handlebars
import com.lsd.core.IdGenerator
import com.lsd.core.adapter.puml.convertToSvgAsync
import com.lsd.core.adapter.puml.toParticipantMarkup
import com.lsd.core.adapter.puml.toPumlMarkup
import com.lsd.core.domain.*
import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.core.report.model.Diagram
import java.lang.System.lineSeparator

data class SequenceDiagramGenerator(
    private val idGenerator: IdGenerator,
    private val events: List<SequenceEvent> = emptyList(),
    private val includes: List<String> = emptyList(),
    private val participants: List<Participant> = emptyList(),
) {
    private val template = Handlebars().compile("templates/puml/sequence-uml")

    fun diagram(maxEventsPerDiagram: Int): Diagram? {
        if (events.isEmpty()) return null

        val uml = generateUml(maxEventsPerDiagram)
        val svg = convertToSvgAsync(markup = uml)
        return Diagram(id = idGenerator.next(), uml = uml, svg = svg)
    }

    private fun generateUml(maxEventsPerDiagram: Int): String {
        return removeLifelineActivationsIfDiagramWillBeSplit(maxEventsPerDiagram)
            .groupedByPages()
            .flatMap { it.chunked(maxEventsPerDiagram) }
            .joinToString(separator = lineSeparator(), transform = ::generateSequenceUml)
    }

    private fun removeLifelineActivationsIfDiagramWillBeSplit(maxEventsPerDiagram: Int) =
        if (events.size > maxEventsPerDiagram || events.any { it is Newpage })
            events.filterNot {
                it is ActivateLifeline || it is DeactivateLifeline
            }
        else events

    private fun generateSequenceUml(events: List<SequenceEvent>): String {
        return template.apply(
            mapOf(
                "theme" to LsdProperties[DIAGRAM_THEME],
                "includes" to includes.distinct(),
                "participants" to participants.usedIn(events).map(Participant::toParticipantMarkup),
                "events" to events.map(SequenceEvent::toPumlMarkup)
            )
        )
    }
}

fun Collection<Participant>.usedIn(events: List<SequenceEvent>): List<Participant> {
    val suppliedParticipantNames = associate { it.componentName.normalisedName to it }
    return events.filterIsInstance<Message>()
        .flatMap { listOf(it.from, it.to) }
        .distinctBy { it.componentName.normalisedName }
        .associateBy { it.componentName.normalisedName }
        .filter { it.key.isNotBlank() }
        .map { eventParticipant ->
            suppliedParticipantNames.getOrDefault(
                key = eventParticipant.key,
                defaultValue = eventParticipant.value
            )
        }
}

fun List<SequenceEvent>.groupedByPages() =
    fold(mutableListOf<MutableList<SequenceEvent>>()
        .also { it.add(ArrayList()) }) { groups, event ->
        when (event) {
            is Newpage -> groups.apply {
                add(ArrayList())
                last().add(event.pageTitle)
            }

            else -> groups.apply { last().add(event) }
        }
    }

