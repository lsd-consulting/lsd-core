package com.lsd.core.builders

import com.lsd.core.builders.LifelineAction.*
import com.lsd.core.domain.*
import com.lsd.core.domain.ParticipantType.*
import java.time.Instant


class LifelineBuilder : SequenceEventBuilder {
    private var participant: Participant = PARTICIPANT.called("")
    private var colour: String = ""
    private var created: Instant = Instant.now()
    private var type: LifelineAction? = null

    fun of(name: String) = apply { of(PARTICIPANT.called(name)) }
    fun of(participant: Participant) = apply { this.participant = participant }
    fun colour(colour: String) = apply { this.colour = colour }
    fun type(type: LifelineAction) = apply { this.type = type }
    fun created(instant: Instant) = apply { this.created = instant }

    override fun build(): SequenceEvent =
        when (type) {
            ACTIVATE -> ActivateLifeline(participant = participant, created = created, colour = colour)
            DEACTIVATE -> DeactivateLifeline(participant = participant, created = created)
            null -> throw IllegalArgumentException("Lifeline type must be specified in the LifelineBuilder!")
        }

    companion object {
        @JvmStatic
        fun activation(): LifelineBuilder = LifelineBuilder().type(ACTIVATE)

        @JvmStatic
        fun deactivation(): LifelineBuilder = LifelineBuilder().type(DEACTIVATE)
    }
}

enum class LifelineAction { ACTIVATE, DEACTIVATE }

infix fun LifelineBuilder.withColour(colour: String): LifelineBuilder = colour(colour)

infix fun LifelineAction.lifeline(participant: String): LifelineBuilder = lifeline(participant.toParticipant())
infix fun LifelineAction.lifeline(participant: Participant): LifelineBuilder =
    LifelineBuilder().of(participant).type(this)