package com.lsd.events;

import com.lsd.IdGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceEventInterpreterTest {
    private final IdGenerator deterministicIdGenerator = new IdGenerator(true);
    private final SequenceEventInterpreter sequenceEventInterpreter = new SequenceEventInterpreter(deterministicIdGenerator);

    @Test
    void interpretBasicMessage() {
        var sequenceEvent = sequenceEventInterpreter.interpret("message from A to B");

        assertThat(sequenceEvent).isEqualTo(Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("")
                .data("")
                .build());
    }

    @Test
    void interpretMessageWithColourAndData() {
        var sequenceEvent = sequenceEventInterpreter.interpret("message from A to B [#red]", "body");

        assertThat(sequenceEvent).isEqualTo(Message.builder()
                .id("1")
                .label("message")
                .from("A")
                .to("B")
                .colour("red")
                .data("body")
                .build());
    }

    @Test
    void handleInvalidPatterns() {
        var sequenceEvent = sequenceEventInterpreter.interpret("This is clearly not a sequence event");

        assertThat(sequenceEvent).isNull();
    }
}