package com.lsd;

import com.lsd.domain.scenario.Fact;
import com.lsd.domain.Report;
import com.lsd.domain.scenario.Scenario;
import com.lsd.domain.scenario.diagram.DiagramGenerator;
import com.lsd.domain.scenario.events.SimpleInteraction;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class HtmlReportApprovalTest {
    private final HtmlReportRenderer htmlReportRenderer = new HtmlReportRenderer();

    @Test
    void renderHtml() {
        String report = htmlReportRenderer.render(Report.builder()
                .title("LSD")
                .scenarios(List.of(
                        Scenario.builder()
                                .title("First Scenario")
                                .description("<pre>Given a Cat\nAnd a dog\nWhen the dog barks\nThen the cat hisses</pre>")
                                .facts(List.of(
                                        Fact.builder()
                                                .key("Greeting")
                                                .value("Hi")
                                                .build())
                                )
                                .sequenceDiagram(DiagramGenerator.builder()
                                        .includes(Set.of())
                                        .participants(List.of())
                                        .events(List.of(
                                                SimpleInteraction.builder()
                                                        .id("111")
                                                        .label("Sending a greeting")
                                                        .from("A")
                                                        .to("B")
                                                        .data("Hi")
                                                        .build(),
                                                SimpleInteraction.builder()
                                                        .id("222")
                                                        .label("Sending a response")
                                                        .from("B")
                                                        .to("A")
                                                        .data("Yo")
                                                        .build()
                                        ))
                                        .build()
                                        .sequenceDiagram())
                                .build(),
                        Scenario.builder()
                                .title("Second Scenario")
                                .description("<p><ul><li>Some details</li><li>Some more details</li></ul></p>")
                                .sequenceDiagram(DiagramGenerator.builder()
                                        .includes(Set.of())
                                        .participants(List.of())
                                        .events(List.of(
                                                SimpleInteraction.builder()
                                                        .id("333")
                                                        .label("An interaction")
                                                        .from("Alpha")
                                                        .to("Beta")
                                                        .data("{\"name\": \"alpha\", \"description\":\"Something long to see how the popup window will respond to text that is wider than the initial box size\"}")
                                                        .build(),
                                                SimpleInteraction.builder()
                                                        .id("444")
                                                        .label("An interaction")
                                                        .from("Beta")
                                                        .to("Gamma")
                                                        .data("β")
                                                        .build(),
                                                SimpleInteraction.builder()
                                                        .id("555")
                                                        .label("An interaction")
                                                        .from("Gamma")
                                                        .to("Delta")
                                                        .data("γ")
                                                        .build()))
                                        .build().sequenceDiagram())
                                .build()))
                .build());

        Approvals.verifyHtml(report);
    }
}