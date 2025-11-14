package com.lsd.core.diagram

import com.lsd.core.IdGenerator
import com.lsd.core.builders.message
import com.lsd.core.domain.ParticipantType.*
import org.approvaltests.Approvals
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class ComponentDiagramGeneratorTest {
    private val idGenerator = IdGenerator(isDeterministic = true)

    @Test
    fun removesDuplicateInteractions() {
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            participants = emptySet(),
            events = buildSet {
                repeat(3) {
                    add("A" to "B" message {
                        label("$it")
                        data("${Random.nextInt()}")
                    })
                }
            },
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun replacesParticipantKeywordWithComponent() {
        val sampleParticipant = PARTICIPANT.called("SomeService")
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = setOf("A" to "B" message {}),
            participants = setOf(sampleParticipant)
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun convertsToValidComponentNames() {
        val sampleParticipant = PARTICIPANT.called("some service / name")
        val diagramGenerator =
            ComponentDiagramGenerator(
                idGenerator = idGenerator,
                events = setOf("A" to "B" message "Hi"),
                participants = setOf(sampleParticipant)
            )

        Approvals.verify(diagramGenerator.diagram()!!.uml)
    }

    @Test
    fun convertsToValidComponentNamesForFromAndTo() {
        val diagramGenerator = ComponentDiagramGenerator(
            idGenerator = idGenerator,
            events = setOf(
                "fixes from" to "fixes to" message {}
            ),
            participants = emptySet()
        )

        Approvals.verify(diagramGenerator.diagram()?.uml)
    }

    @Test
    fun mapsMessageEventsToParticipants() {
        val messages = setOf(
            "A" to "B" message {},
            "B" to "B" message {},
            "C" to "B" message {},
            "D" to "B" message {},
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

        val messages = setOf(
            "Jake" to fred1 message {},
            "Fred" to "Bettie" message {},
            PARTICIPANT.called("Fred", "Fredster", "red") to "Bettie" message {}
        )

        assertThat(participants.usedIn(messages))
            .containsExactly(
                PARTICIPANT.called("Jake"),
                fred2,
                PARTICIPANT.called("Bettie"),
            )
    }
}
