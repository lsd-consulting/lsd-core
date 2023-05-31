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
    fun messageMetricsAreAdded() {
        repeat(25) {
            context.capture(
                messageBuilder().from("A").to("B").build(),
                messageBuilder().from("B").to("A").type(SYNCHRONOUS_RESPONSE).build()
            )
        }
        context.completeScenario("scenario")

        val metrics = whenMetricsAreGenerated()

        assertThat(metrics).isNotNull
            .contains(
                Metric("Events captured", "50"),
                Metric("Messages captured", "50"),
                Metric("SYNCHRONOUS messages captured", "25"),
                Metric("SYNCHRONOUS_RESPONSE messages captured", "25"),
            )
    }

    @Test
    fun messageDurationMetricsAreAdded() {
        context.capture(
            messageBuilder().id("1").from("A").to("B").label("sing song 2").duration(ofSeconds(8)).build(),
            messageBuilder().id("2").from("A").to("B").label("sing song 1").duration(ofSeconds(10)).build(),
            messageBuilder().id("3").from("A").to("C").label("yell loudly").duration(ofSeconds(11)).type(LOST).build(),
            messageBuilder().id("4").from("A").to("B").label("abc").duration(ofSeconds(7)).build(),
            messageBuilder().id("5").from("A").to("B").label("abc").duration(ofSeconds(6)).build(),
            messageBuilder().id("6").from("A").to("B").label("abc").duration(ofSeconds(5)).build(),
            messageBuilder().id("7").from("A").to("B").label("abc").duration(ofSeconds(4)).build(),
            messageBuilder().id("8").from("A").to("D").label("whisper").type(BI_DIRECTIONAL).build(),
        )
        context.completeScenario("duration scenario")

        val metrics = whenMetricsAreGenerated()

        assertThat(metrics).isNotNull
            .contains(
                Metric(
                    "SYNCHRONOUS messages with duration - top",
                    """<a href="#2">10s [A -> B]</a><br><a href="#1">8s [A -> B]</a><br><a href="#4">7s [A -> B]</a><br><a href="#5">6s [A -> B]</a><br><a href="#6">5s [A -> B]</a>"""
                )
            )
            .contains(Metric("SYNCHRONOUS messages with duration - mean", "6.666s [6 duration(s)]"))
            .contains(Metric("LOST messages with duration - top", """<a href="#3">11s [A -> C]</a>"""))
            .contains(Metric("LOST messages with duration - mean", "11s [1 duration(s)]"))
            .doesNotHave(anyMetricWithName("BI_DIRECTIONAL messages with duration - top"))
            .doesNotHave(anyMetricWithName("BI_DIRECTIONAL messages with duration - mean"))
    }

    private fun anyMetricWithName(expectedName: String) =
        Condition<List<Metric>>({ metric -> metric.any { it.name == expectedName } },
            "any metric with expected name: $expectedName"
        )

    @Test
    fun diagramDurationMetricsAreAdded() {
        context.completeScenario("duration scenario")

        val metrics = whenMetricsAreGenerated()

        assertThat(metrics?.find { it.name == "Time for generating component diagram" }).isNotNull
        assertThat(metrics?.find { it.name == "Time for generating sequence diagram" }).isNotNull
    }

    private fun whenMetricsAreGenerated(): List<Metric>? {
        val report = context.buildReport("report", showMetrics = true)
        return report.scenarios.single().metrics?.asList()
    }

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