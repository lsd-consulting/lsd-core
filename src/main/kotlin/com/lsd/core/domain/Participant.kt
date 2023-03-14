package com.lsd.core.domain

data class Participant(
    var type: ParticipantType,
    val component: ComponentName,
    val alias: String?
)

enum class ParticipantType {
    ACTOR,
    BOUNDARY,
    COLLECTIONS,
    CONTROL,
    DATABASE,
    ENTITY,
    PARTICIPANT,
    QUEUE;

    fun called(name: String) = called(name = name, alias = null)

    fun called(name: String, alias: String?) =
        Participant(component = ComponentName(name), type = this, alias = alias)
}