package com.lsd.core.adapter.parse

import com.lsd.core.IdGenerator
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class SequenceSynchronousMessageParserTest {

    private val deterministicIdGenerator = IdGenerator(true)
    private val parser: Parser = SynchronousMessageParser(deterministicIdGenerator)

    @ParameterizedTest
    @ValueSource(strings = ["This is clearly not a sequence event", "sync response from A to B", "message fromA to B"])
    fun shouldNotParseInvalidPatterns(invalidInput: String) {
        Assertions.assertThat(parser.parse(invalidInput, "")).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["response from A to B", "response from A to B [#red]", "response some label from A to B", "response some label from A to B [#red]"])
    fun shouldParseValidPatterns(validInput: String) {
        Assertions.assertThat(parser.parse(validInput, "")).isNotNull
    }

    @Test
    fun parseMessage() {
        val event = parser.parse("message from A to B", "")
        Assertions.assertThat(event).isEqualTo(
            messageBuilder()
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
        Assertions.assertThat(sequenceEvent).isEqualTo(
            messageBuilder()
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