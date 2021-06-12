package com.lsd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SanitiserTest {

    @Test
    void handleNullValuesSafely() {
        assertThat(Sanitiser.sanitise(null)).isEqualTo("");
    }

    @Test
    void removesPlanuUmlMarkup() {
        assertThat(Sanitiser.sanitise("input <$something>")).isEqualTo("input ");

    }
}