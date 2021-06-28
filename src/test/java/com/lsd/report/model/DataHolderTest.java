package com.lsd.report.model;

import com.lsd.events.Message;
import com.lsd.properties.LsdProperties;
import org.junit.jupiter.api.Test;

import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;
import static org.assertj.core.api.Assertions.assertThat;

class DataHolderTest {
    private final DataHolder dataHolder = Message.builder().label(" contains whitespace before and after ").build();
    private final int abbreviationLength = LsdProperties.getInt(LABEL_MAX_WIDTH);

    @Test
    void stripsWhitespaceFromAbbreviatedLabel() {
        assertThat(dataHolder.abbreviatedLabel()).isEqualTo("contains whitespace before and after");
    }

    @Test
    void abbreviatesLabelToMaxLength() {
        var messageWithLongLabel = Message.builder().label("a".repeat(1000)).build();

        assertThat(messageWithLongLabel.abbreviatedLabel().length()).isEqualTo(abbreviationLength);
    }
}