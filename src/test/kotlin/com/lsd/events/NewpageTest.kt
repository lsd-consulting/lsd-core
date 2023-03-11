package com.lsd.events

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NewpageTest {
    
    @Test
    fun produceExpectedMarkup() {
        assertThat(Newpage.titled("my title").toMarkup())
            .isEqualTo("\nnewpage 'my title'\n")
    }

    @Test
    fun handleEmptyTitle() {
        assertThat(Newpage.titled("").toMarkup())
            .isEqualTo("\nnewpage\n")
    }
}