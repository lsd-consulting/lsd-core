package com.lsd.report.approval;

import com.lsd.IdGenerator;
import com.lsd.ParticipantType;
import com.lsd.diagram.DiagramGenerator;
import com.lsd.events.Message;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.events.ArrowType.BI_DIRECTIONAL_DOTTED;
import static com.lsd.events.ArrowType.DOTTED;

class PlantUmlRendererApprovalTest {

    @Test
    void renderSequenceUml() {
        Approvals.verify(diagramGenerator().sequenceDiagram().getUml());
    }

    private DiagramGenerator diagramGenerator() {
        return DiagramGenerator.builder()
                .idGenerator(new IdGenerator(true))
                .includes(new LinkedHashSet<>(List.of(
                        "tupadr3/font-awesome-5/clock",
                        "tupadr3/font-awesome-5/database"
                )))
                .participants(List.of(
                        ParticipantType.ACTOR.called("Arnold", "Arnie"),
                        ParticipantType.BOUNDARY.called("Nick"),
                        ParticipantType.CONTROL.called("A control"),
                        ParticipantType.COLLECTIONS.called("some collection"),
                        ParticipantType.ENTITY.called("Entity"),
                        ParticipantType.PARTICIPANT.called("Party"),
                        ParticipantType.QUEUE.called("Party"),
                        ParticipantType.DATABASE.called("Derek", "D for danger")
                ))
                .events(List.of(
                        Message.builder()
                                .id("1")
                                .label("Sending a request")
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
                                .build()
                )).build();
    }
}