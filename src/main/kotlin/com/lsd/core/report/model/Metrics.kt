package com.lsd.core.report.model

import com.lsd.core.domain.Message
import com.lsd.core.domain.SequenceEvent
import java.time.Duration
import java.util.regex.Pattern

class Metrics(
    val events: List<SequenceEvent>,
    val sequenceDuration: Duration,
    val componentDuration: Duration
) {

    fun asList(): List<Metric> {
        val allMessages = events.filterIsInstance(Message::class.java)
        val messagesByType = allMessages.groupBy { it.type }
        return listOf(
            Metric("Events captured", "${events.size}"),
            Metric("Time for generating sequence diagram", sequenceDuration.pretty()),
            Metric("Time for generating component diagram", componentDuration.pretty()),
            Metric("Messages captured", "${allMessages.size}"),
        ) + messagesByType.keys.flatMap { type ->
            val messagesOfType = messagesByType[type].orEmpty()
            val durationMessages = messagesOfType.filter { it.duration != null }

            listOf(
                Metric("$type messages captured", "${messagesOfType.size}"),
            ) + if (durationMessages.isNotEmpty()) listOf(
                Metric("$type messages total duration", durationMessages.let(::totalDuration)),
                Metric("$type messages with duration - top", durationMessages.let(::topDurations).durationString()),
                Metric("$type messages with duration - mean", durationMessages.let(::meanDuration)),
            ) else emptyList()
        }
    }

    private fun topDurations(messages: List<Message>): List<Message> {
        return messages
            .sortedBy { it.duration }
            .reversed()
            .take(5)
    }

    private fun meanDuration(messages: List<Message>): String {
        val durationMessages = messages.filter { it.duration != null }
        val average = durationMessages.map { it.duration?.toMillis() ?: 0 }.average()
        val averageDuration = Duration.ofMillis(average.toLong())
        return "${averageDuration.pretty()} [${durationMessages.size} duration(s)]"
    }

    private fun totalDuration(messages: List<Message>): String {
        val durationMessages = messages.filter { it.duration != null }
        val total = durationMessages.sumOf { it.duration?.toMillis() ?: 0 }
        val totalDuration = Duration.ofMillis(total)
        return "${totalDuration.pretty()} [${durationMessages.size} duration(s)]"
    }

}

private fun Duration.pretty(): String = toString()
    .substring(2)
    .replace(Pattern.compile("(\\d[HMS])(?!$)").toRegex(), "$1 ")
    .lowercase()

private fun List<Message>.durationString(): String {
    return joinToString(separator = "<br>") {
        """<a href="#${it.id}">${it.duration?.pretty() ?: "0s"} [${it.from.componentName.normalisedName} -> ${it.to.componentName.normalisedName}]</a>"""
    }
}

data class Metric(val name: String, val value: String)
    