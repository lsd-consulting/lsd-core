package com.lsd.approval;

import com.lsd.domain.scenario.diagram.DiagramGenerator;
import com.lsd.domain.scenario.events.Message;
import com.lsd.domain.scenario.events.ResponseMessage;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.domain.ParticipantType.*;

class PlantUmlRendererApprovalTest {

    @Test
    void renderSequenceUml() {
        Approvals.verify(diagramGenerator().sequenceDiagram().getUml());
    }

    private DiagramGenerator diagramGenerator() {
        return DiagramGenerator.builder()
                .includes(new LinkedHashSet<>(List.of(
                        "tupadr3/font-awesome-5/clock",
                        "tupadr3/font-awesome-5/database"
                )))
                .participants(List.of(
                        ACTOR.called("Arnold", "Arnie"),
                        BOUNDARY.called("Nick"),
                        CONTROL.called("A control"),
                        COLLECTIONS.called("some collection"),
                        ENTITY.called("Entity"),
                        PARTICIPANT.called("Party"),
                        QUEUE.called("Party"),
                        DATABASE.called("Derek", "D for danger")
                ))
                .events(List.of(
                        Message.builder()
                                .id("1")
                                .label("Sending a request")
                                .from("A")
                                .to("B")
                                .data("Please help?")
                                .build(),
                        ResponseMessage.builder()
                                .id("2")
                                .label("Sending a response with a long label that should be abbreviated if all goes well")
                                .from("B")
                                .to("A")
                                .data("OK")
                                .build()
                )).build();
    }
}