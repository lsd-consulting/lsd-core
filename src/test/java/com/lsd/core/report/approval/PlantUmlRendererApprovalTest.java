package com.lsd.core.report.approval;

import com.lsd.core.IdGenerator;
import com.lsd.core.diagram.SequenceDiagramGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static com.lsd.core.builders.MessageBuilder.messageBuilder;
import static com.lsd.core.domain.MessageType.BI_DIRECTIONAL;
import static com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE;
import static com.lsd.core.domain.ParticipantType.*;
import static org.approvaltests.Approvals.verify;

class PlantUmlRendererApprovalTest {

    @Test
    void renderSequenceUml() {
        var diagram = Objects.requireNonNull(sequenceDiagramGenerator().diagram(2));
        verify(diagram.getUml());
    }

    private SequenceDiagramGenerator sequenceDiagramGenerator() {
        return new SequenceDiagramGenerator(
                new IdGenerator(true),
                List.of(
                        messageBuilder()
                                .id("1")
                                .label(" Sending a request ")
                                .from("A")
                                .to("B")
                                .data("Please help?")
                                .build(),
                        messageBuilder()
                                .id("2")
                                .label("Sending a response with a long label that should be abbreviated if all goes well")
                                .from("B")
                                .to("A")
                                .data("OK")
                                .type(SYNCHRONOUS_RESPONSE)
                                .build(),
                        messageBuilder()
                                .id("3")
                                .label("On the phone")
                                .from("B")
                                .to("A")
                                .type(BI_DIRECTIONAL)
                                .build()),
                List.of(
                        "tupadr3/font-awesome-5/clock",
                        "tupadr3/font-awesome-5/database"
                ),
                List.of(
                        ACTOR.called("Arnold", "Arnie"),
                        BOUNDARY.called("Nick"),
                        CONTROL.called("A control"),
                        COLLECTIONS.called("some collection"),
                        ENTITY.called("Entity"),
                        PARTICIPANT.called("Party"),
                        QUEUE.called("Party"),
                        QUEUE.called("Party"), // Duplicate participant to test dropping duplicates
                        DATABASE.called("Derek", "D for danger")
                ));
    }
}