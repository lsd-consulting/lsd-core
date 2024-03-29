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
        is NoteOver -> noteOverMarkup()
        is PageTitle -> pageTitleMarkup()
        is TimeDelay -> timeDelayMarkup()
        is VerticalSpace -> verticalSpaceMarkup()
        is ActivateLifeline -> activateMarkup()
        is DeactivateLifeline -> deactivateMarkup()
    }

private fun LogicalDivider.logicalDividerMarkup(): String = "== $label =="

private fun VerticalSpace.verticalSpaceMarkup(): String = size?.let { "||$it||" } ?: "|||"

private fun TimeDelay.timeDelayMarkup(): String = label?.let { "...$label..." } ?: "..."

private fun NoteLeft.noteLeftMarkup(): String =
    ofParticipant?.let { "note left of ${it.componentName.normalisedName}: $note" } ?: "note left: $note"

private fun NoteRight.noteRightMarkup(): String =
    ofParticipant?.let { "note right of ${it.componentName.normalisedName}: $note" } ?: "note right: $note"

private fun NoteOver.noteOverMarkup(): String = "note over ${participant.componentName.normalisedName}: $note"
    
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
            """${from.componentName.normalisedName} ${arrowMarkup()} ${to.componentName.normalisedName}: ${
                labelMarkup(
                    id,
                    label
                )
            }"""

        SHORT_OUTBOUND ->
            """${from.componentName.normalisedName} ${arrowMarkup()}?: ${labelMarkup(id, label)}"""

        SHORT_INBOUND ->
            """?${arrowMarkup()} ${to.componentName.normalisedName}: ${labelMarkup(id, label)}"""
    }

private fun Message.labelMarkup(id: String, label: String): String {
    return if (colour.isBlank()) {
        """[[#${id} {${label.sanitise()}} ${label.abbreviate()}]]"""
    } else {
        """<text fill="$colour">[[#${id} {${label.sanitise()}} ${label.abbreviate()}]]</text>"""
    }
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
    "${type.name.lowercase()} ${componentName.normalisedName}${alias?.let { " as \"$alias\"" } ?: ""}${colour?.let { " #$colour" } ?: ""}"

private fun ActivateLifeline.activateMarkup(): String =
    "activate ${participant.componentName.normalisedName}${this.colour?.let { "#$it" } ?: ""}"

private fun DeactivateLifeline.deactivateMarkup(): String = "deactivate ${participant.componentName.normalisedName}"