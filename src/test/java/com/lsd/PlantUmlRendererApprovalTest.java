package com.lsd;

import com.lsd.domain.interactions.SimpleInteraction;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlantUmlRendererApprovalTest {
    private final PlantUmlRenderer renderer = new PlantUmlRenderer();

    @Test
    void renderPlantUml() {
        String markup = renderer.render(List.of(
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
        ));

        Approvals.verify(markup);
    }
}