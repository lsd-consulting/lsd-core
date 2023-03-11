package com.lsd.report.approval;

import com.lsd.IdGenerator;
import com.lsd.diagram.SequenceDiagramGenerator;
import com.lsd.events.Message;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static com.lsd.ParticipantType.*;
import static com.lsd.events.ArrowType.*;

class PlantUmlRendererApprovalTest {

    @Test
    void renderSequenceUml() {
        var diagram = Objects.requireNonNull(sequenceDiagramGenerator().diagram(2));
        Approvals.verify(diagram.getUml());
    }

    private SequenceDiagramGenerator sequenceDiagramGenerator() {
        return new SequenceDiagramGenerator(
                new IdGenerator(true),
                List.of(
                        Message.builder()
                                .id("1")
                                .label(" Sending a request ")
                                .from("A")
                                .to("B")
                                .data("Please help?")
                                .build(),
                        Message.builder()
                                .id("2")
                                .label("Sending a response with a long label that should be abbreviated if all goes well")
                                .from("B")
                                .to("A")
                                .data("OK")
                                .arrowType(DOTTED)
                                .build(),
                        Message.builder()
                                .id("3")
                                .label("On the phone")
                                .from("B")
                                .to("A")
                                .arrowType(BI_DIRECTIONAL_DOTTED)
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