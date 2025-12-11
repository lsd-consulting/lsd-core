package com.lsd.core.report.model

import com.lsd.core.builders.MessageBuilder
import com.lsd.core.builders.SequenceEventBuilder
import com.lsd.core.builders.messages
import com.lsd.core.builders.withDuration
import com.lsd.core.builders.withType
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
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
                ("A" messages "B").build(),
                ("B" messages "A").build()
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
                "A" messages "B",
                "B" messages "C",
                "C" messages "B" withDuration 3.seconds withType SYNCHRONOUS_RESPONSE,
                "B" messages "C",
                "C" messages "D",
                "D" messages "C" withDuration 1.seconds withType SYNCHRONOUS_RESPONSE,
                "C" messages "B" withDuration 1.seconds,
                "B" messages "C" withDuration 2.seconds withType SYNCHRONOUS_RESPONSE,
                "B" messages "A" withDuration 10.seconds withType SYNCHRONOUS_RESPONSE,
            ).map(SequenceEventBuilder::build),
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
                "A" messages "B",
                "C" messages "D",
                "C" messages "B" withDuration 3.seconds,
                "B" messages "A" withDuration 5.seconds,
                "D" messages "C" withDuration 4.seconds,
            ).map(MessageBuilder::build),
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
                "A" messages "B" withDuration 8.seconds,
                "A" messages "B" withDuration 10.seconds,
                "A" messages "C" withDuration 11.seconds,
                "A" messages "B" withDuration 7.seconds,
                "A" messages "B" withDuration 6.seconds,
                "A" messages "B" withDuration 5.seconds,
                "A" messages "B" withDuration 4.seconds,
                "A" messages "D"
            ).map(MessageBuilder::build),
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