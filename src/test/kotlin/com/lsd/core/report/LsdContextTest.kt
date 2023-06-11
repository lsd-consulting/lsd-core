package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.ActivateLifelineBuilder
import com.lsd.core.builders.DeactivateLifelineBuilder
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.MessageType.*
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
import com.lsd.core.report.model.Metric
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import java.time.Duration.ofSeconds

class LsdContextTest {

    private val context = LsdContext()

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToSize() {
        repeat(10) {
            context.capture(messageBuilder().from("A").to("B").build())
            context.capture(ActivateLifelineBuilder.activation().of("A").build())
            context.capture(messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
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
        context.capture(messageBuilder().from("A").to("B").build())
        context.capture(ActivateLifelineBuilder.activation().of("A").build())
        context.capture(messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
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

    @Test
    fun activationsKeptWhenDiagramsAreNotSplit() {
        context.capture(messageBuilder().from("A").to("B").build())
        context.capture(ActivateLifelineBuilder.activation().of("A").build())
        context.capture(messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
        context.capture(DeactivateLifelineBuilder.deactivation().of("A").build())
        context.completeScenario("scenario")

        val report = context.buildReport("report")
        val sequenceUml = report.scenarios.single().sequenceDiagram?.uml

        assertThat(sequenceUml)
            .contains("activate", "deactivate")
            .contains("A -> B")
    }
}