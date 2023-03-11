package com.lsd

import com.lsd.builders.CapturedScenarioBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CapturedScenarioTest {
    private val capturedScenario = CapturedScenarioBuilder().build()

    @Test
    fun excludeNullEvents() {
        capturedScenario.add(null)
        assertThat(capturedScenario.sequenceEvents).isEmpty()
    }
}