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
                    "Children duration 6s",
                    "Total duration 10s",
                    "0s [A -> B]",
                    "10s [B -> A]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 3s",
                    "Children duration 0s",
                    "Total duration 3s",
                    "0s [B -> C]",
                    "3s [C -> B]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 2s",
                    "Children duration 0s",
                    "Total duration 2s",
                    "2s [B -> C]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 1s",
                    "Children duration 1s",
                    "Total duration 2s",
                    "0s [C -> D]",
                    "1s [D -> C]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 0s",
                    "Children duration 1s",
                    "Total duration 1s",
                    "0s [B -> C]",
                    "1s [C -> B]",
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
                    "Children duration 0s",
                    "Total duration 4s",
                    "4s [D -> C]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 3s",
                    "Children duration 0s",
                    "Total duration 3s",
                    "3s [C -> B]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 2s",
                    "Children duration 3s",
                    "Total duration 5s",
                    "0s [A -> B]",
                    "5s [B -> A]",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 0s",
                    "Children duration 0s",
                    "Total duration 0s",
                    "0s [C -> D]",
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
            .has(anyMetricWithName("Top isolated durations"))
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 11s",
                    "Children duration 0s",
                    "Total duration 11s",
                    "11s [A -> C]"
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "Isolated duration 10s",
                    "Children duration 0s",
                    "Total duration 10s",
                    "10s [A -> B]"
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