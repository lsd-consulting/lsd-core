package com.lsd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SanitiserTest {

    @Test
    void handleNullValuesSafely() {
        assertThat(Sanitiser.sanitise(null)).isEqualTo("");
    }

    @Test
    void removesPlantUmlMarkup() {
        assertThat(Sanitiser.sanitise("input <$something>")).isEqualTo("input ");
    }

    @Test
    void trimLeadingWhitespace() {
        assertThat(Sanitiser.sanitise(" abc")).isEqualTo("abc");
    }

    @Test
    void trimTrailingWhitespace() {
        assertThat(Sanitiser.sanitise("abc ")).isEqualTo("abc");
    }
}