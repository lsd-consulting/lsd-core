package com.lsd.core.adapter.puml

import com.lsd.core.abbreviate
import com.lsd.core.domain.*
import com.lsd.core.domain.MessageType.*
import com.lsd.core.sanitise
import java.lang.System.lineSeparator

fun SequenceEvent.toPumlMarkup(): String =
    when (this) {
        is LogicalDivider -> logicalDividerMarkup()
        is Message -> sequenceMessageMarkup()
        is Newpage -> newpageMarkup()
        is NoteLeft -> noteLeftMarkup()
        is NoteRight -> noteRightMarkup()
        is PageTitle -> pageTitleMarkup()
        is TimeDelay -> timeDelayMarkup()
        is VerticalSpace -> verticalSpaceMarkup()
    }

private fun LogicalDivider.logicalDividerMarkup(): String = "== $label =="

private fun VerticalSpace.verticalSpaceMarkup(): String = size?.let { "||$it||" } ?: "|||"

private fun TimeDelay.timeDelayMarkup(): String = label?.let { "...$label..." } ?: "..."

private fun NoteLeft.noteLeftMarkup(): String =
    ofParticipant?.let { "note left of ${it.component.name}: $note" } ?: "note left: $note"

private fun NoteRight.noteRightMarkup(): String =
    ofParticipant?.let { "note right of ${it.component.name}: $note" } ?: "note right: $note"

private fun PageTitle.pageTitleMarkup() =
    "${lineSeparator()}title ${title.ifBlank { "<title missing>" }}${lineSeparator()}"

private fun Newpage.newpageMarkup() =
    "${lineSeparator()}newpage${pageTitle.title.let { if (it.isEmpty()) "" else " '$it'" }}${lineSeparator()}"

private fun Message.sequenceMessageMarkup() =
    when (type) {
        SYNCHRONOUS_RESPONSE,
        BI_DIRECTIONAL,
        LOST,
        ASYNCHRONOUS,
        SYNCHRONOUS ->
            """${from.name} ${arrowMarkup()} ${to.name}: <text fill="$colour">[[#${id} {${label.sanitise()}} ${label.abbreviate()}]]</text>"""

        SHORT_OUTBOUND ->
            """${from.name} ${arrowMarkup()}?: <text fill="$colour">[[#${id} {${label.sanitise()}} ${label.abbreviate()}]]</text>"""

        SHORT_INBOUND ->
            """?${arrowMarkup()} ${to.name}: <text fill="$colour">[[#${id} {${label.sanitise()}} ${label.abbreviate()}]]</text>"""
    }

private fun Message.arrowMarkup(): String {
    val c = if (colour.isBlank()) "" else "[#$colour]"
    return when (type) {
        SHORT_OUTBOUND,
        SHORT_INBOUND,
        SYNCHRONOUS -> "-$c>"
        ASYNCHRONOUS -> "-$c>>"
        SYNCHRONOUS_RESPONSE -> "--$c>"
        LOST -> "-$c>x"
        BI_DIRECTIONAL -> "<-$c>"
    }
}

fun Participant.toParticipantMarkup(): String =
    "${type.name.lowercase()} ${component.name}${alias?.let { " as \"$alias\"" } ?: ""}"
