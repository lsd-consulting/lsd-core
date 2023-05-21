package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.MessageBuilder
import com.lsd.core.domain.Fact
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LsdContextTest {

    private val context = LsdContext()

    @Test
    fun metricsAreAddedToFacts() {
        repeat(25) {
            context.capture(MessageBuilder.messageBuilder().from("A").to("B").build())
            context.capture(MessageBuilder.messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
        }
        context.completeScenario("scenario")

        val report = context.buildReport("report", showMetrics = true)

        val facts = report.scenarios.single().facts
        assertThat(facts).contains(
            Fact("Total events captured", "50"),
            Fact("Total messages captured", "50"),
            Fact("Total SYNCHRONOUS messages captured", "25"),
            Fact("Total SYNCHRONOUS_RESPONSE messages captured", "25")
        )
        assertThat(facts.find { it.key == "Time for generating component diagram" }).isNotNull
        assertThat(facts.find { it.key == "Time for generating sequence diagram" }).isNotNull
    }
}