package com.lsd.core.diagram

import com.lsd.core.IdGenerator
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.Message
import com.lsd.core.domain.ParticipantType.*
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun mapsMessageEventsToParticipants() {
        val messages = listOf(
            messageBuilder().from("A").to("B").build(),
            messageBuilder().from("B").to("B").build(),
            messageBuilder().from("C").to("B").build(),
            messageBuilder().from("D").to("B").build(),
        )

        val participants = listOf(
            DATABASE.called("D", "dan", colour = "red"),
            BOUNDARY.called("B", "ben"),
            PARTICIPANT.called("unused")
        )

        assertThat(participants.usedIn(messages))
            .containsExactlyInAnyOrder(
                DATABASE.called("D", "dan", colour = "red"),
                BOUNDARY.called("B", "ben"),
                PARTICIPANT.called("A"),
                PARTICIPANT.called("C")
            )
    }

    @Test
    fun participantTypeAndAliasCanBeOverriddenInParticipantsButNotEvents() {
        val fred1 = CONTROL.called("Fred")
        val fred2 = DATABASE.called("Fred", "Freddy", "purple")
        val participants = listOf(fred1, fred2)

        val messages = listOf(
            messageBuilder().from(PARTICIPANT.called("Jake")).to(fred1).build(),
            messageBuilder().from("Fred").to("Bettie").build(),
            messageBuilder().from(PARTICIPANT.called("Fred", "Fredster", "red")).to("Bettie").build()
        )
        
        assertThat(participants.usedIn(messages))
            .containsExactly(
                PARTICIPANT.called("Jake"),
                fred2,
                PARTICIPANT.called("Bettie"),
            )
    }

    private fun sampleMessage(): Message {
        return messageBuilder()
            .from("A")
            .to("B")
            .build()
    }
}

