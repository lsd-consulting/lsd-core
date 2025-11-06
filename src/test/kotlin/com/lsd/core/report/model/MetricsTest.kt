package com.lsd.core.report.model

import com.lsd.core.builders.messages
import com.lsd.core.domain.NoteLeft
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

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
                ("A" messages "B") {},
                ("B" messages "A") {}
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
                ("A" messages "B") {},
                ("B" messages "C") {},
                ("C" messages "B") { duration(3.seconds) },
                ("B" messages "C") {},
                ("C" messages "D") {},
                ("D" messages "C") { duration(1.seconds) },
                ("C" messages "B") { duration(1.seconds) },
                ("B" messages "C") { duration(2.seconds) },
                ("B" messages "A") { duration(10.seconds) },
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
                ("A" messages "B") {},
                ("C" messages "D") {},
                ("C" messages "B") { duration(3.seconds) },
                ("B" messages "A") { duration(5.seconds) },
                ("D" messages "C") { duration(4.seconds) },
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
                ("A" messages "B") { duration(8.seconds) },
                ("A" messages "B") { duration(10.seconds) },
                ("A" messages "C") { duration(11.seconds) },
                ("A" messages "B") { duration(7.seconds) },
                ("A" messages "B") { duration(6.seconds) },
                ("A" messages "B") { duration(5.seconds) },
                ("A" messages "B") { duration(4.seconds) },
                ("A" messages "D") {}
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