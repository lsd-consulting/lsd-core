package com.lsd.parse;

import com.lsd.IdGenerator;
import com.lsd.events.SynchronousResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SynchronousResponseParserTest {
    private final IdGenerator deterministicIdGenerator = new IdGenerator(true);
    private final Parser parser = new SynchronousResponseParser(deterministicIdGenerator);

    @ParameterizedTest
    @ValueSource(strings = {
            "This is clearly not a sequence event",
            "sync response from A",
            "message from A to B"
    })
    void shouldNotParseInvalidPatterns(String invalidInput) {
        assertThat(parser.parse(invalidInput, null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "sync response from A to B",
            "sync message from A to B [#red]",
            "sync some label from A to B",
            "sync from A to B [#red]"
    })
    void shouldParseValidPatterns(String validInput) {
        assertThat(parser.parse(validInput, null)).isPresent();
    }

    @Test
    void parseSynchronousResponse() {
        var event = parser.parse("sync call from A to B", "");

        assertThat(event).isEqualTo(Optional.of(SynchronousResponse.builder()
                .id("1")
                .label("call")
                .from("A")
                .to("B")
                .colour("")
                .data("")
                .build()));
    }

    @Test
    void parseSynchronousResponseWithColour() {
        var event = parser.parse("sync response message from A to B [#red]", "body");

        assertThat(event).isEqualTo(Optional.of(SynchronousResponse.builder()
                .id("1")
                .label("response message")
                .from("A")
                .to("B")
                .colour("red")
                .data("body")
                .build()));
    }
}