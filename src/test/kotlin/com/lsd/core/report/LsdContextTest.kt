package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.ActivateLifelineBuilder
import com.lsd.core.builders.DeactivateLifelineBuilder
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant.now

class LsdContextTest {

    private val context = LsdContext()

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToSize() {
        repeat(10) {
            context.capture(messageBuilder().from("A").to("B").build())
            context.capture(ActivateLifelineBuilder.activation().of("A").created(now()).build())
            context.capture(messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
            context.capture(DeactivateLifelineBuilder.deactivation().of("A").created(now()).build())
        }
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml(maxEventsPerDiagram = 5))
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

        assertThat(generatedSequenceUml())
            .doesNotContain("activate", "deactivate")
            .doesNotContain("New page here")
            .contains("A -> B")
    }

    @Test
    fun activationsKeptWhenDiagramsAreNotSplit() {
        context.capture(messageBuilder().from("A").to("B").build())
        context.capture(ActivateLifelineBuilder.activation().of("A").build())
        context.capture(messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build())
        context.capture(DeactivateLifelineBuilder.deactivation().of("A").build())
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml())
            .contains("activate", "deactivate")
            .contains("A -> B")
    }

    private fun generatedSequenceUml(maxEventsPerDiagram: Int = 100): String? {
        val report = context.buildReport(
            title = "report",
            options = ReportOptions(
                devMode = false,
                metricsEnabled = false,
                maxEventsPerDiagram = maxEventsPerDiagram
            ),
        )
        return report.scenarios.single().sequenceDiagram?.uml
    }
}