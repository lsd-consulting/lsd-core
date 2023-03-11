package com.lsd.diagram

import com.github.jknack.handlebars.Handlebars
import com.lsd.IdGenerator
import com.lsd.builders.SequenceDiagramGeneratorBuilder
import com.lsd.events.SequenceEvent
import com.lsd.events.groupedByPages
import com.lsd.properties.LsdProperties
import com.lsd.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.report.model.Diagram
import com.lsd.report.model.Participant
import org.apache.commons.collections4.ListUtils
import java.lang.System.lineSeparator
import java.util.stream.Collectors.joining
import java.util.stream.Collectors.toList

data class SequenceDiagramGenerator(
    private val idGenerator: IdGenerator,
    private val events: List<SequenceEvent> = emptyList(),
    private val includes: List<String> = emptyList(),
    private val participants: List<Participant> = emptyList(),
) {
    private val template = Handlebars().compile("templates/sequence-uml")

    fun diagram(maxEventsPerDiagram: Int): Diagram? {
        val uml = generateUml(maxEventsPerDiagram)
        return if (events.isEmpty()) null else {
            Diagram(
                id = idGenerator.next(),
                uml = uml,
                svg = convertToSvg(markup = uml)
            )
        }
    }

    private fun generateUml(maxEventsPerDiagram: Int): String =
        events.groupedByPages()
            .stream()
            .flatMap { ListUtils.partition(it, maxEventsPerDiagram).stream() }
            .map { generateSequenceUml(it) }
            .collect(joining(lineSeparator()))

    private fun generateSequenceUml(events: List<SequenceEvent>): String {
        return template.apply(
            mapOf(
                "theme" to LsdProperties.get(DIAGRAM_THEME),
                "includes" to includes.distinct(),
                "participants" to participants.stream()
                    .distinct()
                    .collect(toList()),
                "events" to events.stream()
                    .map(SequenceEvent::toMarkup)
                    .collect(toList())
            )
        )
    }

    companion object {
        fun builder(): SequenceDiagramGeneratorBuilder = SequenceDiagramGeneratorBuilder()
    }
}

