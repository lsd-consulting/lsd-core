package com.lsd.core.diagram

import com.lsd.core.IdGenerator
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.Message
import com.lsd.core.domain.ParticipantType.ACTOR
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test

internal class ComponentDiagramGeneratorTest {
    private val idGenerator = IdGenerator(isDeterministic = true)

    @Test
    fun removesDuplicateInteractions() {
        val sampleMessage = sampleMessage()
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = listOf(sampleMessage, sampleMessage, sampleMessage),
            participants = emptyList()
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun removesDuplicateParticipants() {
        val sampleParticipant = ACTOR.called("Nick")
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator,
            listOf(sampleMessage()),
            listOf(sampleParticipant, sampleParticipant)
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun replacesParticipantKeywordWithComponent() {
        val sampleParticipant = PARTICIPANT.called("SomeService")
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = listOf(sampleMessage()),
            participants = listOf(sampleParticipant)
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun convertsToValidComponentNames() {
        val sampleParticipant = PARTICIPANT.called("some service / name")
        val diagramGenerator =
            ComponentDiagramGenerator(
                idGenerator = idGenerator,
                events = listOf(sampleMessage()),
                participants = listOf(sampleParticipant)
            )

        Approvals.verify(diagramGenerator.diagram()!!.uml)
    }

    @Test
    fun convertsToValidComponentNamesForFromAndTo() {
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = listOf(
                messageBuilder()
                    .from("fixes from")
                    .to("fixes to")
                    .build()
            ), participants = emptyList()
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    private fun sampleMessage(): Message {
        return messageBuilder()
            .from("A")
            .to("B")
            .build()
    }
}