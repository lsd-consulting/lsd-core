package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.ActivateLifelineBuilder
import com.lsd.core.builders.DeactivateLifelineBuilder
import com.lsd.core.builders.MessageBuilder
import com.lsd.core.domain.Fact
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
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

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToSize() {
        repeat(10) {
            context.capture(MessageBuilder.messageBuilder().from("A").to("B").build())
            context.capture(ActivateLifelineBuilder.activation().of("A").build())
            context.capture(MessageBuilder.messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
            context.capture(DeactivateLifelineBuilder.deactivation().of("A").build())
        }
        context.completeScenario("scenario")

        val report = context.buildReport("report", maxEventsPerDiagram = 5)
        val sequenceUml = report.scenarios.single().sequenceDiagram?.uml

        assertThat(sequenceUml)
            .doesNotContain("activate", "deactivate")
            .contains("A -> B")
    }

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToNewpage() {
        context.capture(MessageBuilder.messageBuilder().from("A").to("B").build())
        context.capture(ActivateLifelineBuilder.activation().of("A").build())
        context.capture(MessageBuilder.messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
        context.capture(Newpage(PageTitle("New page here")))
        context.capture(DeactivateLifelineBuilder.deactivation().of("A").build())
        context.completeScenario("scenario")

        val report = context.buildReport("report")
        val sequenceUml = report.scenarios.single().sequenceDiagram?.uml

        assertThat(sequenceUml)
            .doesNotContain("activate", "deactivate")
            .contains("New page here")
            .contains("A -> B")
    }
}