package com.lsd.core.domain.events

import com.lsd.core.diagram.groupedByPages
import com.lsd.core.domain.*
import com.lsd.core.domain.ParticipantType.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GroupByNewPageEventsTest {

    @Test
    fun splitsStreamByNewPageEvents() {
        val participantA = PARTICIPANT.called("A")
        val participantB = PARTICIPANT.called("B")
        val participantC = PARTICIPANT.called("C")

        val message1 = Message(id = "1", from = participantB, to = participantA)
        val message2 = Message(id = "2", from = participantA, to = participantB)
        val message3 = Message(id = "3", from = participantB, to = participantC)

        val sequenceEvents = listOf(
            message1,
            message2,
            Newpage(PageTitle("Part B")),
            message3
        )

        Assertions.assertThat(sequenceEvents.groupedByPages()).containsExactly(
            mutableListOf(
                message1,
                message2
            ),
            mutableListOf(
                PageTitle("Part B"),
                message3
            )
        )
    }
}