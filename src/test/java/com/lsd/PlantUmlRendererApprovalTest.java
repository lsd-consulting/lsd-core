package com.lsd;

import com.lsd.domain.scenario.diagram.DiagramGenerator;
import com.lsd.domain.scenario.events.Message;
import com.lsd.domain.scenario.events.ResponseMessage;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.lsd.domain.ParticipantType.*;

class PlantUmlRendererApprovalTest {

    private DiagramGenerator diagramGenerator = DiagramGenerator.builder()
            .includes(Set.of())
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
                            .label("Sending a response")
                            .from("B")
                            .to("A")
                            .data("OK")
                            .build()
            )).build();

    @Test
    void renderSequenceUml() {
        Approvals.verify(diagramGenerator.sequenceDiagram().getUml());
    }

    @Test
    void renderSequenceSvg() {
        Approvals.verifyXml(diagramGenerator.sequenceDiagram().toSvg());
    }
}