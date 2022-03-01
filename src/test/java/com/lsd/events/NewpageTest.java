package com.lsd.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NewpageTest {

    @Test
    void produceExpectedMarkup() {
        var markup = Newpage.titled("my title").toMarkup();

        assertThat(markup).isEqualTo("\nnewpage 'my title'\n");
    }

    @Test
    void handleEmptyTitle() {
        var markup = Newpage.titled("").toMarkup();

        assertThat(markup).isEqualTo("\nnewpage\n");
    }
    
    @Test
    void handleNullTitle() {
        var markup = Newpage.titled(null).toMarkup();

        assertThat(markup).isEqualTo("\nnewpage\n");
    }
}