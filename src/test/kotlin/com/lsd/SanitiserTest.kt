package com.lsd

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class SanitiserTest {
    @Test
    fun handleNullValuesSafely() {
        Assertions.assertThat(sanitise(null)).isEqualTo("")
    }

    @Test
    fun removesPlantUmlMarkup() {
        Assertions.assertThat(sanitise("input <\$something>")).isEqualTo("input ")
    }

    @Test
    fun trimLeadingWhitespace() {
        Assertions.assertThat(sanitise(" abc")).isEqualTo("abc")
    }

    @Test
    fun trimTrailingWhitespace() {
        Assertions.assertThat(sanitise("abc ")).isEqualTo("abc")
    }
}