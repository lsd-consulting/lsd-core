package com.lsd.core.builders

import com.lsd.core.domain.*
import com.lsd.core.domain.LifelineAction.*
import com.lsd.core.domain.ParticipantType.*
import java.time.Instant


class LifelineBuilder : SequenceEventBuilder {
    private var participant: Participant = PARTICIPANT.called("")
    private var colour: String? = null
    private var created: Instant = Instant.now()
    private var action: LifelineAction = ACTIVATE

    fun of(name: String) = apply { this.participant = PARTICIPANT.called(name) }
    fun of(participant: Participant) = apply { this.participant = participant }
    fun colour(colour: String) = apply { this.colour = colour }
    fun type(type: LifelineAction) = apply { this.action = type }
    fun created(instant: Instant) = apply { this.created = instant }

    override fun build(): Lifeline = Lifeline(
        participant = participant,
        created = created,
        colour = colour,
        action = action
    )

    companion object {
        @JvmStatic
        fun activation(): LifelineBuilder = LifelineBuilder().type(ACTIVATE)

        @JvmStatic
        fun deactivation(): LifelineBuilder = LifelineBuilder().type(DEACTIVATE)
    }
}

infix fun LifelineBuilder.withColour(colour: String): LifelineBuilder = colour(colour)

infix fun LifelineAction.lifeline(participant: String) = lifeline(participant.toParticipant())
infix fun LifelineAction.lifeline(participant: Participant) = LifelineBuilder().of(participant).type(this)
