package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.ActivateLifelineBuilder.Companion.activation
import com.lsd.core.builders.DeactivateLifelineBuilder.Companion.deactivation
import com.lsd.core.builders.messages
import com.lsd.core.builders.withLabel
import com.lsd.core.builders.withType
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LsdContextTest {

    private val context = LsdContext()

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToSize() {
        repeat(10) {
            context.capture("A" messages "B")
            context.capture(activation().of("A"))
            context.capture("B" messages "A" withType SYNCHRONOUS_RESPONSE)
            context.capture(deactivation().of("A"))
        }
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml(maxEventsPerDiagram = 5))
            .doesNotContain("activate", "deactivate")
            .contains("A -> B")
    }

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToNewpage() {
        context.capture("A" messages "B")
        context.capture(activation().of("A"))
        context.capture("B" messages "A" withType SYNCHRONOUS_RESPONSE)
        context.capture(Newpage(PageTitle("New page here")))
        context.capture(deactivation().of("A").build())
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml())
            .doesNotContain("activate", "deactivate")
            .doesNotContain("New page here")
            .contains("A -> B")
    }

    @Test
    fun activationsKeptWhenDiagramsAreNotSplit() {
        context.capture("A" messages "B")
        context.capture(activation().of("A").build())
        context.capture("B" messages "A" withLabel "OK" withType SYNCHRONOUS_RESPONSE)
        context.capture(deactivation().of("A").build())
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