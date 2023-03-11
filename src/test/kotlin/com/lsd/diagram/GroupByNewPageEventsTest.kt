package com.lsd.diagram

import com.lsd.events.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GroupByNewPageEventsTest {
    
    @Test
    fun splitsStreamByNewPageEvents() {
        val message1 = Message(id = "1", label = "", from = "B", to = "A")
        val message2 = Message(id = "2", label = "", from = "A", to = "B")
        val message3 = Message(id = "3", label = "", from = "B", to = "C")

        val sequenceEvents = listOf(
            message1,
            message2,
            Newpage.titled("Part B"),
            message3
        )

        assertThat(sequenceEvents.groupedByPages()).containsExactly(
            mutableListOf(
                message1, 
                message2
            ),
            mutableListOf(
                PageTitle.titled("Part B"),
                message3)
        )
    }
}