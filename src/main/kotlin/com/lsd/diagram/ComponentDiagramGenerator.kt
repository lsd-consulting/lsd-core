package com.lsd.diagram

import com.github.jknack.handlebars.Handlebars
import com.lsd.IdGenerator
import com.lsd.events.Message
import com.lsd.events.SequenceEvent
import com.lsd.properties.LsdProperties
import com.lsd.properties.LsdProperties.DIAGRAM_THEME
import com.lsd.report.model.Diagram
import com.lsd.report.model.Participant

class ComponentDiagramGenerator(
    private val idGenerator: IdGenerator,
    private val events: List<SequenceEvent>,
    private val participants: List<Participant> = emptyList()
) {
    private val template = Handlebars().compile("templates/component-uml")

    fun diagram(): Diagram? {
        return if (events.isEmpty()) null else {
            val uml = generateUml()
            return Diagram(
                id = idGenerator.next(),
                uml = uml,
                svg = convertToSvg(markup = uml)
            )
        }
    }

    private fun generateUml(): String {
        return template.apply(mapOf(
            "theme" to LsdProperties[DIAGRAM_THEME],
            "participants" to participants
                .map(Participant::markup)
                .map { it.replace("^participant ".toRegex(), "component ") }
                .distinct(),
            "events" to events
                .asSequence()
                .filterIsInstance<Message>()
                .map { "[${ValidComponentName.of(it.from)}] --> ${ValidComponentName.of(it.to)}" }
                .distinct()
                .toList()
        ))
    }
}