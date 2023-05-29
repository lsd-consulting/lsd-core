package com.lsd.core.report

import com.lsd.core.LsdContext
import com.lsd.core.builders.ActivateLifelineBuilder
import com.lsd.core.builders.DeactivateLifelineBuilder
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.MessageType.LOST
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Newpage
import com.lsd.core.domain.PageTitle
import com.lsd.core.report.model.Metric
import org.assertj.core.api.Assertions.assertThat
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
            messageBuilder().from("A").to("B").label("sing song 1").duration(ofSeconds(10)).build(),
            messageBuilder().from("A").to("B").label("sing song 2").duration(ofSeconds(8)).build(),
            messageBuilder().from("A").to("C").label("yell loudly").duration(ofSeconds(11)).type(LOST).build(),
        )
        context.completeScenario("duration scenario")

        val metrics = whenMetricsAreGenerated()

        assertThat(metrics).isNotNull
            .contains(Metric("SYNCHRONOUS messages max duration", "10s : sing song 1 [A -> B]"))
            .contains(Metric("SYNCHRONOUS messages mean duration", "9s [2 message(s) with duration]"))
            .contains(Metric("LOST messages max duration", "11s : yell loudly [A -> C]"))
            .contains(Metric("LOST messages mean duration", "11s [1 message(s) with duration]"))
    }
    
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