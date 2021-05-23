package com.lsd;

import com.lsd.domain.scenario.diagram.DiagramGenerator;
import com.lsd.domain.scenario.events.SimpleInteraction;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class PlantUmlRendererApprovalTest {

    private DiagramGenerator diagramGenerator = DiagramGenerator.builder()
            .participants(List.of())
            .includes(Set.of())
            .events(List.of(
                    SimpleInteraction.builder()
                            .id("1")
                            .label("Sending a request")
                            .from("A")
                            .to("B")
                            .data("Please help?")
                            .build(),
                    SimpleInteraction.builder()
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