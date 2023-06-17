package com.lsd.core.report.model

import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.NoteLeft
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Duration.ofSeconds

class MetricsTest {

    @Test
    fun timeToGenerateDiagrams() {
        val metrics = Metrics(events = listOf(), sequenceDuration = Duration.ZERO, componentDuration = Duration.ZERO)

        assertThat(metrics.asList()).isNotNull
            .contains(Metric(name = "Time to generate component diagram", value = "0s"))
            .contains(Metric(name = "Time to generate sequence diagram", value = "0s"))
    }

    @Test
    fun capturedEventsAndMessages() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                NoteLeft(note = "hi"),
                messageBuilder().from("A").to("B").build(),
                messageBuilder().from("B").to("A").build()
            ),
        )

        assertThat(metrics.asList()).isNotNull
            .contains(
                Metric("Events captured", "3"),
                Metric("Messages captured", "2"),
            )
    }

    @Test
    fun isolatedDurationsExample1() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                message(from = "A", to = "B"),
                message(from = "B", to = "C"),
                message(from = "C", to = "B", duration = 3),
                message(from = "B", to = "C"),
                message(from = "C", to = "D"),
                message(from = "D", to = "C", duration = 1),
                message(from = "C", to = "B", duration = 1),
                message(from = "B", to = "C", duration = 2),
                message(from = "B", to = "A", duration = 10),
            ),
        )

        assertThat(metrics.asList()).isNotNull
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 4s",
                    "Children 6s",
                    "Total 10s",
                    "[A -> B] 0s",
                    "[B -> A] 10s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 3s",
                    "[B -> C] 0s",
                    "[C -> B] 3s",
                    "Total 3s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 2s",
                    "[B -> C] 2s",
                    "Total 2s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 1s",
                    "[C -> D] 0s",
                    "[D -> C] 1s",
                    "Children 1s",
                    "Total 2s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 0s",
                    "[B -> C] 0s",
                    "[C -> B] 1s",
                    "Children 1s",
                    "Total 1s",
                )
            )
    }

    @Test
    fun isolatedDurationsExample2() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                message(from = "A", to = "B"),
                message(from = "C", to = "D"),
                message(from = "C", to = "B", duration = 3),
                message(from = "B", to = "A", duration = 5),
                message(from = "D", to = "C", duration = 4),
            ),
        )

        assertThat(metrics.asList()).isNotNull
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 4s",
                    "[D -> C] 4s ",
                    "Total 4s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 3s",
                    "[C -> B] 3s",
                    "Total 3s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 2s",
                    "[A -> B] 0s",
                    "[B -> A] 5s",
                    "Children 3s",
                    "Total 5s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 0s",
                    "[C -> D] 0s",
                    "Total 0s",
                )
            )

    }

    @Test
    fun isolatedDurationsExample3() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                message(from = "A", to = "B", duration = 8),
                message(from = "A", to = "B", duration = 10),
                message(from = "A", to = "C", duration = 11),
                message(from = "A", to = "B", duration = 7),
                message(from = "A", to = "B", duration = 6),
                message(from = "A", to = "B", duration = 5),
                message(from = "A", to = "B", duration = 4),
                message(from = "A", to = "D"),
            ),
        )

        assertThat(metrics.asList(max = 2)).isNotNull
            .has(anyMetricWithName("Top bottlenecks"))
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 11s",
                    "[A -> C] 11s",
                    "Total 11s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 10s",
                    "[A -> B] 10s",
                    "Total 10s",
                )
            )
    }

    private fun message(from: String, to: String, duration: Long = 0) =
        messageBuilder().from(from).to(to).duration(ofSeconds(duration)).build()


    private fun anyMetricWithName(expectedName: String) =
        Condition<List<Metric>>(
            { metric -> metric.any { it.name == expectedName } },
            "any metric with expected name: $expectedName"
        )

    private fun anyMetricWithValueContaining(vararg substring: String) =
        Condition<List<Metric>>(
            { metric ->
                metric.any { substring.all { s -> it.value.contains(s) } }
            },
            "any metric value containing substrings: ${substring.joinToString()}}"
        )
}