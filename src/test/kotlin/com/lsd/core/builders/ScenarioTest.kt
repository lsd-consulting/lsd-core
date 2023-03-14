package com.lsd.core.builders

import com.lsd.core.builders.ScenarioBuilder.Companion.scenarioBuilder
import com.lsd.core.domain.SequenceEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScenarioTest {
    private val scenario = scenarioBuilder().build()

    @Test
    fun excludeNullEvents() {
        null?.let<SequenceEvent, Boolean> { scenario.events.add(it) }
        assertThat(scenario.events).isEmpty()
    }
}