package com.lsd.core.domain.events

import com.lsd.core.diagram.groupedByPages
import com.lsd.core.domain.ComponentName
import com.lsd.core.domain.Message
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GroupByNewPageEventsTest {

    @Test
    fun splitsStreamByNewPageEvents() {
        val message1 = Message(id = "1", label = "", from = ComponentName("B"), to = ComponentName("A"))
        val message2 = Message(id = "2", label = "", from = ComponentName("A"), to = ComponentName("B"))
        val message3 = Message(id = "3", label = "", from = ComponentName("B"), to = ComponentName("C"))

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