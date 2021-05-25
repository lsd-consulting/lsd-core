package com.lsd.approval;

import com.lsd.HtmlReportRenderer;
import com.lsd.domain.Report;
import com.lsd.domain.scenario.Fact;
import com.lsd.domain.scenario.Scenario;
import com.lsd.domain.scenario.diagram.DiagramGenerator;
import com.lsd.domain.scenario.events.Markup;
import com.lsd.domain.scenario.events.Message;
import com.lsd.domain.scenario.events.NoteLeft;
import com.lsd.domain.scenario.events.ResponseMessage;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.lsd.domain.ParticipantType.*;

class HtmlReportApprovalTest {
    private final HtmlReportRenderer htmlReportRenderer = new HtmlReportRenderer();

    @Test
    void renderHtml() {
        String report = htmlReportRenderer.render(buildSampleReport());

        Approvals.verifyHtml(report);
    }

    private Report buildSampleReport() {
        return Report.builder()
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
                                        .includes(new LinkedHashSet<>(List.of(
                                                "tupadr3/font-awesome-5/hamburger", 
                                                "tupadr3/font-awesome-5/heart"
                                        )))
                                        .participants(List.of(
                                                ACTOR.called("A", "Arnie"),
                                                BOUNDARY.called("Unused participant"),
                                                BOUNDARY.called("Another Unused participant", "G"),
                                                DATABASE.called("B", "Badboy \\nDB")
                                        ))
                                        .events(List.of(
                                                Message.builder()
                                                        .id("111")
                                                        .label("Sending food <$hamburger{scale=0.4}>")
                                                        .from("A")
                                                        .to("B")
                                                        .data("Hi")
                                                        .build(),
                                                new Markup("..."),
                                                ResponseMessage.builder()
                                                        .id("222")
                                                        .label("Sending a response")
                                                        .from("B")
                                                        .to("A")
                                                        .data("Thank You!")
                                                        .build(),
                                                new NoteLeft("Friends <$heart{scale=0.4}>")
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
                                                Message.builder().id("333").label("An interaction").from("Alpha").to("Beta").data("{\"name\": \"alpha\", \"description\":\"Something long to see how the popup window will respond to text that is wider than the initial box size\"}").build(),
                                                Message.builder().id("444").label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").build(),
                                                Message.builder().id("555").label("An interaction").from("Gamma").to("Delta").data("γ").build()))
                                        .build().sequenceDiagram())
                                .build()))
                .build();
    }
}