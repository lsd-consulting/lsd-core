package com.lsd.core.adapter.parse

import com.lsd.core.IdGenerator
import com.lsd.core.builders.MessageBuilder.Companion.messageBuilder
import com.lsd.core.domain.MessageType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SynchronousResponseParserTest {

    private val deterministicIdGenerator = IdGenerator(true)
    private val parser: Parser = SynchronousResponseParser(deterministicIdGenerator)

    @ParameterizedTest
    @ValueSource(strings = ["This is clearly not a sequence event", "sync response from A", "message from A to B"])
    fun shouldNotParseInvalidPatterns(invalidInput: String) {
        Assertions.assertThat(parser.parse(invalidInput, "")).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["sync response from A to B", "sync message from A to B [#red]", "sync some label from A to B", "sync from A to B [#red]"])
    fun shouldParseValidPatterns(validInput: String) {
        Assertions.assertThat(parser.parse(validInput, "")).isNotNull
    }

    @Test
    fun parseSynchronousResponse() {
        val event = parser.parse("sync call from A to B", "")
        Assertions.assertThat(event).isEqualTo(
            messageBuilder()
                .id("1")
                .label("call")
                .from("A")
                .to("B")
                .colour("")
                .data("")
                .type(MessageType.SYNCHRONOUS_RESPONSE)
                .build()
        )
    }

    @Test
    fun parseSynchronousResponseWithColour() {
        val event = parser.parse("sync response message from A to B [#red]", "body")
        Assertions.assertThat(event).isEqualTo(
            messageBuilder()
                .id("1")
                .label("response message")
                .from("A")
                .to("B")
                .colour("red")
                .data("body")
                .type(MessageType.SYNCHRONOUS_RESPONSE)
                .build()
        )
    }
}