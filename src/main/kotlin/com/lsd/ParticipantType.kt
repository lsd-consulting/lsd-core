package com.lsd

import com.lsd.diagram.ValidComponentName
import com.lsd.report.model.Participant

enum class ParticipantType(private val type: String) {
    ACTOR("actor"), 
    BOUNDARY("boundary"), 
    COLLECTIONS("collections"), 
    CONTROL("control"), 
    DATABASE("database"), 
    ENTITY("entity"), 
    PARTICIPANT("participant"),
    QUEUE("queue");

    fun called(name: String): Participant {
        return Participant("$type ${ValidComponentName.of(name)}")
    }

    fun called(name: String, alias: String): Participant {
        return Participant("$type ${ValidComponentName.of(name)} as \"$alias\"")
    }
}