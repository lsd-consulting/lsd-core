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
        ) + messagesByType.keys.flatMap {
            listOf(
                Metric("$it messages captured", "${messagesByType[it]?.size}"),
                Metric("$it messages max duration", messagesByType[it]?.let(::longestDuration) ?: "NA"),
                Metric("$it messages mean duration", messagesByType[it]?.let(::meanDuration) ?: "NA"),
            )
        }
    }

    private fun longestDuration(messages: List<Message>): String {
        val longestDurationMessage = messages.maxByOrNull { it.duration ?: Duration.ZERO }
        return longestDurationMessage?.let {
            "${it.duration?.pretty() ?: "0s"} : ${longestDurationMessage.label} [${it.from.componentName.normalisedName} -> ${it.to.componentName.normalisedName}]"
        } ?: ""
    }

    private fun meanDuration(messages: List<Message>): String {
        val durationMessages = messages.filter { it.duration != null }
        val average = durationMessages.map { it.duration?.toMillis() ?: 0 }.average()
        val averageDuration = Duration.ofMillis(average.toLong())
        return "${averageDuration.pretty()} [${durationMessages.size} message(s) with duration]"
    }

    private fun Duration.pretty(): String = toString()
        .substring(2)
        .replace(Pattern.compile("(\\d[HMS])(?!$)").toRegex(), "$1 ")
        .lowercase()
}

data class Metric(val name: String, val value: String)
    