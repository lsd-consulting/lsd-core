package com.lsd.core.domain

data class Participant
@JvmOverloads constructor(
    var type: ParticipantType = ParticipantType.PARTICIPANT,
    val componentName: ComponentName,
    val alias: String? = null,
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

    @JvmOverloads fun called(name: String, alias: String? = null, colour: String? = null) =
        Participant(componentName = ComponentName(name), type = this, alias = alias, colour = colour)
}