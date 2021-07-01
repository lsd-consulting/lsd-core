package com.lsd.diagram;

import com.lsd.IdGenerator;
import com.lsd.events.Message;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.lsd.ParticipantType.ACTOR;
import static com.lsd.ParticipantType.PARTICIPANT;

class ComponentDiagramGeneratorTest {

    private final IdGenerator idGenerator = new IdGenerator(true);

    @Test
    void removesDuplicateInteractions() {
        var sampleMessage = sampleMessage();
        var diagramGenerator = new ComponentDiagramGenerator(List.of(), List.of(sampleMessage, sampleMessage, sampleMessage), idGenerator);
        var diagram = diagramGenerator.diagram().orElseThrow();

        Approvals.verify(diagram.getUml());
    }

    @Test
    void removesDuplicateParticipants() {
        var sampleParticipant = ACTOR.called("Nick");
        var diagramGenerator = new ComponentDiagramGenerator(List.of(sampleParticipant, sampleParticipant), List.of(sampleMessage()), idGenerator);
        var diagram = diagramGenerator.diagram().orElseThrow();

        Approvals.verify(diagram.getUml());
    }

    @Test
    void replacesParticipantKeywordWithComponent() {
        var sampleParticipant = PARTICIPANT.called("SomeService");
        var diagramGenerator = new ComponentDiagramGenerator(List.of(sampleParticipant), List.of(sampleMessage()), idGenerator);
        var diagram = diagramGenerator.diagram().orElseThrow();

        Approvals.verify(diagram.getUml());
    }

    private Message sampleMessage() {
        return Message.builder()
                .from("A")
                .to("B")
                .build();
    }
}