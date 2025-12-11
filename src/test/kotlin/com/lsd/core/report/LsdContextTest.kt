package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.ReportOptions
import com.lsd.core.builders.*
import com.lsd.core.domain.LifelineAction.ACTIVATE
import com.lsd.core.domain.LifelineAction.DEACTIVATE
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.Newpage.Companion.title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LsdContextTest {

    private val context = LsdContext()

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToSize() {
        repeat(10) {
            context.capture(
                "A" messages "B",
                ACTIVATE lifeline "A",
                "B" messages "A" withType SYNCHRONOUS_RESPONSE,
                DEACTIVATE lifeline "A",
            )
        }
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml(maxEventsPerDiagram = 5))
            .doesNotContain("activate", "deactivate")
            .contains("A -> B")
    }

    @Test
    fun activationsRemovedWhenDiagramsAreSplitDueToNewpage() {
        context.capture("A" messages "B")
        context.capture(ACTIVATE lifeline "A" withColour "red")
        context.capture("B" messages "A" withType SYNCHRONOUS_RESPONSE)
        context.capture(Newpage title "New page here")
        context.capture(DEACTIVATE lifeline "A")
        context.completeScenario("scenario")

        assertThat(generatedSequenceUml())
            .doesNotContain("activate", "deactivate")
            .doesNotContain("New page here")
            .contains("A -> B")
    }

    @Test
    fun activationsKeptWhenDiagramsAreNotSplit() {
        context.capture(
            "A" messages "B",
            ACTIVATE lifeline "A" withColour "blue",
            "B" messages "A" withLabel "OK" withType SYNCHRONOUS_RESPONSE,
            DEACTIVATE lifeline "A"
        )
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