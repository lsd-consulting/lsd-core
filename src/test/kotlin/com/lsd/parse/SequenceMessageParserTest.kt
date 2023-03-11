package com.lsd.parse

import com.lsd.IdGenerator
import com.lsd.events.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class SequenceMessageParserTest {

    private val deterministicIdGenerator = IdGenerator(true)
    private val parser: Parser = OutboundMessageParser(deterministicIdGenerator)

    @ParameterizedTest
    @ValueSource(strings = ["This is clearly not a sequence event", "sync response from A to B", "message fromA to B"])
    fun shouldNotParseInvalidPatterns(invalidInput: String) {
        assertThat(parser.parse(invalidInput, "")).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["response from A to B", "response from A to B [#red]", "response some label from A to B", "response some label from A to B [#red]"])
    fun shouldParseValidPatterns(validInput: String) {
        assertThat(parser.parse(validInput, "")).isNotNull
    }

    @Test
    fun parseMessage() {
        val event = parser.parse("message from A to B", "")
        assertThat(event).isEqualTo(
            Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("")
                .data("")
                .build()
        )
    }

    @Test
    fun parseMessageWithColourAndData() {
        val sequenceEvent = parser.parse("message from A to B [#red]", "some data")
        assertThat(sequenceEvent).isEqualTo(
            Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("red")
                .data("some data")
                .build()
        )
    }
}