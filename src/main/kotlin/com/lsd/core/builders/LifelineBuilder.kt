package com.lsd.core.builders

import com.lsd.core.domain.ActivateLifeline
import com.lsd.core.domain.ComponentName
import com.lsd.core.domain.DeactivateLifeline
import com.lsd.core.domain.Participant

class ActivateLifelineBuilder {
    private var lifeline = ActivateLifeline(component = ComponentName(""))

    fun of(name: String) = also { of(ComponentName(name)) }
    fun of(participant: Participant) = also { of(participant.componentName) }
    fun of(componentName: ComponentName) = also { lifeline = lifeline.copy(component = componentName) }
    fun colour(colour: String) = also { lifeline = lifeline.copy(colour = colour) }
    fun build(): ActivateLifeline = lifeline

    companion object {
        @JvmStatic
        fun activation(): ActivateLifelineBuilder = ActivateLifelineBuilder()
    }
}

class DeactivateLifelineBuilder {
    private var lifeline = DeactivateLifeline(component = ComponentName(""))

    fun of(name: String) = also { of(ComponentName(name)) }
    fun of(participant: Participant) = also { of(participant.componentName) }
    fun of(componentName: ComponentName) = also { lifeline = lifeline.copy(component = componentName) }
    fun build(): DeactivateLifeline = lifeline

    companion object {
        @JvmStatic
        fun deactivation(): DeactivateLifelineBuilder = DeactivateLifelineBuilder()
    }
}
