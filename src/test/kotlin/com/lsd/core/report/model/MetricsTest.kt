package com.lsd.core.report.model

import com.lsd.core.builders.message
import com.lsd.core.builders.reply
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
            .contains(Metric(key = "Time to generate component diagram", value = "0s"))
            .contains(Metric(key = "Time to generate sequence diagram", value = "0s"))
    }

    @Test
    fun capturedEventsAndMessages() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                NoteLeft(note = "hi"),
                "A" to "B" message {},
                "B" to "A" message {}
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
                "A" to "B" message {},
                "B" to "C" message {},
                "C" to "B" reply 3.seconds,
                "B" to "C" message {},
                "C" to "D" message {},
                "D" to "C" reply 1.seconds,
                "C" to "B" message 1.seconds,
                "B" to "C" reply 2.seconds,
                "B" to "A" reply 10.seconds,
            ),
        )

        assertThat(metrics.asList()).isNotNull
            .has(
                anyMetricWithValueContaining(
                    "B (isolated duration 4s)",
                    "Children 6s",
                    "Total 10s",
                    "[A -> B] 0s",
                    "[B -> A] 10s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "C (isolated duration 3s)",
                    "[B -> C] 0s",
                    "[C -> B] 3s",
                    "Total 3s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "C (isolated duration 2s)",
                    "[B -> C] 2s",
                    "Total 2s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "D (isolated duration 1s)",
                    "[C -> D] 0s",
                    "[D -> C] 1s",
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
                "A" to "B" message {},
                "C" to "D" message {},
                "C" to "B" message 3.seconds,
                "B" to "A" message 5.seconds,
                "D" to "C" message 4.seconds,
            ),
        )

        assertThat(metrics.asList()).isNotNull
            .has(
                anyMetricWithValueContaining(
                    "C (isolated duration 4s)",
                    "[D -> C] 4s ",
                    "Total 4s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "B (isolated duration 3s)",
                    "[C -> B] 3s",
                    "Total 3s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "B (isolated duration 2s)",
                    "[A -> B] 0s",
                    "[B -> A] 5s",
                    "Children 3s",
                    "Total 5s",
                )
            )
    }

    @Test
    fun isolatedDurationsExample3() {
        val metrics = Metrics(
            sequenceDuration = Duration.ZERO,
            componentDuration = Duration.ZERO,
            events = listOf(
                "A" to "B" message 8.seconds,
                "A" to "B" message 10.seconds,
                "A" to "C" message 11.seconds,
                "A" to "B" message 7.seconds,
                "A" to "B" message 6.seconds,
                "A" to "B" message 5.seconds,
                "A" to "B" message 4.seconds,
                "A" to "D" message {}
            ),
        )

        assertThat(metrics.asList(max = 2)).isNotNull
            .has(anyMetricWithName("Top bottlenecks"))
            .has(
                anyMetricWithValueContaining(
                    "C (isolated duration 11s)",
                    "[A -> C] 11s",
                    "Total 11s",
                )
            )
            .has(
                anyMetricWithValueContaining(
                    "B (isolated duration 10s)",
                    "[A -> B] 10s",
                    "Total 10s",
                )
            )
    }

    private fun anyMetricWithName(expectedName: String) =
        Condition<List<Metric>>(
            { metric -> metric.any { it.key == expectedName } },
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