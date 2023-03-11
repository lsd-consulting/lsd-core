package com.lsd.diagram;

import com.lsd.IdGenerator;
import com.lsd.events.SequenceMessage;
import com.lsd.events.Message;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.lsd.ParticipantType.ACTOR;
import static com.lsd.ParticipantType.PARTICIPANT;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

class ComponentDiagramGeneratorTest {

    private final IdGenerator idGenerator = new IdGenerator(true);

    @Test
    void removesDuplicateInteractions() {
        var sampleMessage = sampleMessage();
        var diagramGenerator = new ComponentDiagramGenerator(idGenerator, List.of(sampleMessage, sampleMessage, sampleMessage), List.of());
        var diagram = requireNonNull(diagramGenerator.diagram());

        Approvals.verify(diagram.getUml());
    }

    @Test
    void removesDuplicateParticipants() {
        var sampleParticipant = ACTOR.called("Nick");
        var diagramGenerator = new ComponentDiagramGenerator(idGenerator, List.of(sampleMessage()), List.of(sampleParticipant, sampleParticipant));
        var diagram = requireNonNull(diagramGenerator.diagram());

        Approvals.verify(diagram.getUml());
    }

    @Test
    void replacesParticipantKeywordWithComponent() {
        var sampleParticipant = PARTICIPANT.called("SomeService");
        var diagramGenerator = new ComponentDiagramGenerator(idGenerator, List.of(sampleMessage()), List.of(sampleParticipant));
        var diagram = requireNonNull(diagramGenerator.diagram());

        Approvals.verify(diagram.getUml());
    }

    @Test
    void convertsToValidComponentNames() {
        var sampleParticipant = PARTICIPANT.called("some service / name");
        var diagramGenerator = new ComponentDiagramGenerator(idGenerator, List.of(sampleMessage()), List.of(sampleParticipant));
        var diagram = requireNonNull(diagramGenerator.diagram());

        Approvals.verify(diagram.getUml());
    }

    @Test
    void convertsToValidComponentNamesForFromAndTo() {
        var diagramGenerator = new ComponentDiagramGenerator(idGenerator, List.of(Message.builder()
                .from("fixes from")
                .to("fixes to")
                .build()), emptyList());

        var diagram = requireNonNull(diagramGenerator.diagram());

        Approvals.verify(diagram.getUml());
    }

    private SequenceMessage sampleMessage() {
        return Message.builder()
                .from("A")
                .to("B")
                .build();
    }
}