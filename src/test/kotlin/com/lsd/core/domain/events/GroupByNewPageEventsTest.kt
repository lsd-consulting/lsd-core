package com.lsd.core.domain.events

import com.lsd.core.diagram.groupedByPages
import com.lsd.core.domain.*
import com.lsd.core.domain.ParticipantType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GroupByNewPageEventsTest {

    private val participantA = PARTICIPANT.called("A")
    private val participantB = PARTICIPANT.called("B")
    private val participantC = PARTICIPANT.called("C")

    private val message1 = Message(id = "1", from = participantB, to = participantA)
    private val message2 = Message(id = "2", from = participantA, to = participantB)
    private val message3 = Message(id = "3", from = participantB, to = participantC)

    private val pageTitle = PageTitle("Part B")

    @Test
    fun groupsByNewPageEvents() {
        val groupedEvents = listOf(
            message1,
            message2,
            Newpage(pageTitle),
            message3
        ).groupedByPages()

        assertThat(groupedEvents).containsExactly(
            mutableListOf(message1, message2),
            mutableListOf(pageTitle, message3)
        )
    }

    @Test
    fun newpageAtBeginning() {
        val groupedEvents = listOf(
            Newpage(pageTitle),
            message1,
            message2,
            message3
        ).groupedByPages()

        assertThat(groupedEvents).containsExactly(
            mutableListOf(pageTitle, message1, message2, message3)
        )
    }

    @Test
    fun newpageAtEnd() {
        val groupedEvents = listOf(
            message1,
            message2,
            message3,
            Newpage(pageTitle),
        ).groupedByPages()

        assertThat(groupedEvents).containsExactly(
            mutableListOf(message1, message2, message3),
            mutableListOf(pageTitle)
        )
    }
}