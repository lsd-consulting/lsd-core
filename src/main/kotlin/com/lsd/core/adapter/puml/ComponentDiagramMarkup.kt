package com.lsd.core.adapter.puml

import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType
import com.lsd.core.domain.Message

internal fun Message.toComponentMarkup(): String = "[${from.name}] --> ${to.name}"

internal fun Participant.toComponentMarkup(): String {
    val alias = alias?.let { " as \"$alias\"" } ?: ""
    val typeMarkup = when (type) {
        ParticipantType.PARTICIPANT -> "component"
        ParticipantType.ACTOR,
        ParticipantType.BOUNDARY,
        ParticipantType.COLLECTIONS,
        ParticipantType.CONTROL,
        ParticipantType.DATABASE,
        ParticipantType.ENTITY,
        ParticipantType.QUEUE -> type.name.lowercase()
    }
    return "$typeMarkup ${componentName.name}$alias"
}