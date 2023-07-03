package com.lsd.core.builders

import com.lsd.core.domain.*
import com.lsd.core.domain.ParticipantType.*
import java.time.Instant

class ActivateLifelineBuilder {
    private var lifeline = ActivateLifeline(participant = PARTICIPANT.called(""))

    fun of(name: String) = also { lifeline = lifeline.copy(participant = PARTICIPANT.called(name)) }
    fun of(participant: Participant) = also { lifeline = lifeline.copy(participant = participant) }
    fun colour(colour: String) = also { lifeline = lifeline.copy(colour = colour) }
    fun created(instant: Instant) = also { lifeline = lifeline.copy(created = instant) }
    fun build(): ActivateLifeline = lifeline

    companion object {
        @JvmStatic
        fun activation(): ActivateLifelineBuilder = ActivateLifelineBuilder()
    }
}

class DeactivateLifelineBuilder {
    private var lifeline = DeactivateLifeline(participant = PARTICIPANT.called(""))

    fun of(name: String) = also { lifeline = lifeline.copy(participant = PARTICIPANT.called(name)) }
    fun of(participant: Participant) = also { lifeline = lifeline.copy(participant = participant) }
    fun created(instant: Instant) = also { lifeline = lifeline.copy(created = instant) }
    fun build(): DeactivateLifeline = lifeline

    companion object {
        @JvmStatic
        fun deactivation(): DeactivateLifelineBuilder = DeactivateLifelineBuilder()
    }
}
