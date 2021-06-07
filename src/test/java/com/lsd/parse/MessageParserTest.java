package com.lsd.parse;

import com.lsd.IdGenerator;
import com.lsd.events.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MessageParserTest {
    private final IdGenerator deterministicIdGenerator = new IdGenerator(true);
    private final Parser parser = new MessageParser(deterministicIdGenerator);

    @ParameterizedTest
    @ValueSource(strings = {
            "This is clearly not a sequence event",
            "sync response from A to B",
            "message fromA to B"
    })
    public void shouldNotParseInvalidPatterns(String invalidInput) {
        assertThat(parser.parse(invalidInput, null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "response from A to B",
            "response from A to B [#red]",
            "response some label from A to B",
            "response some label from A to B [#red]"
    })
    void shouldParseValidPatterns(String validInput) {
        assertThat(parser.parse(validInput, null)).isPresent();
    }

    @Test
    void parseMessage() {
        var event = parser.parse("message from A to B", null);

        assertThat(event).isEqualTo(Optional.of(Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("")
                .data("")
                .build()));
    }

    @Test
    void parseMessageWithColourAndData() {
        var sequenceEvent = parser.parse("message from A to B [#red]", "some data");

        assertThat(sequenceEvent).isEqualTo(Optional.of(Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("red")
                .data("some data")
                .build()));
    }
}