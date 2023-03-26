package com.lsd.core.domain

data class Participant(
    var type: ParticipantType,
    val componentName: ComponentName,
    val alias: String?,
    val colour: String? = null
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

    fun called(name: String, alias: String? = null) =
        Participant(componentName = ComponentName(name), type = this, alias = alias)

    fun called(name: String, alias: String? = null, colour: String? = null) =
        Participant(componentName = ComponentName(name), type = this, alias = alias, colour = colour)
}