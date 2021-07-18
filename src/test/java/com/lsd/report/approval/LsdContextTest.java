package com.lsd.report.approval;

import com.lsd.IdGenerator;
import com.lsd.LsdContext;
import com.lsd.events.*;
import com.lsd.report.model.Participant;
import com.lsd.report.model.PopupContent;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.OutcomeStatus.*;
import static com.lsd.ParticipantType.*;
import static com.lsd.events.ArrowType.*;
import static j2html.TagCreator.*;

class LsdContextTest {

    private final LsdContext lsdContext = LsdContext.getInstance();
    private final IdGenerator idGenerator = lsdContext.getIdGenerator();

    private LinkedHashSet<String> additionalIncludes = new LinkedHashSet<>(List.of(
            "tupadr3/font-awesome-5/hamburger",
            "tupadr3/font-awesome-5/heart"
    ));

    private final List<Participant> participants = List.of(
            ACTOR.called("A", "Arnie"),
            BOUNDARY.called("Unused participant"),
            DATABASE.called("B", "Badboy\\nDB")
    );

    @BeforeEach
    public void clearContext() {
        lsdContext.clear();
    }

    @Test
    void createsReportWithScenariosAndEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);

        lsdContext.capture(ShortMessageInbound.builder().id(nextId()).to("A").label("in").data("start some job").build());
        lsdContext.capture(Message.builder().id(nextId()).from("A").to("B").label("Message 1").data("some data 1").arrowType(BI_DIRECTIONAL).build());
        lsdContext.capture(ShortMessageOutbound.builder().id(nextId()).from("B").label("out").data("some data 1").build());
        lsdContext.capture(Message.builder().id(nextId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").arrowType(LOST).build());
        lsdContext.capture(SynchronousResponse.builder().id(nextId()).label("A synchronous response").from("Gamma").to("Beta").data("200 OK").build());
        lsdContext.completeScenario("A Success scenario", "First scenario description", SUCCESS);

        lsdContext.capture(Message.builder().id(nextId()).label("Sending food <$hamburger{scale=0.4}>").from("A").to("B").colour("orange").arrowType(DOTTED_THIN).build());
        lsdContext.capture(new Markup("..."));
        lsdContext.capture("Sending a response from B to A [#red]", "Thank You!");
        lsdContext.capture(new NoteLeft("Friends <$heart{scale=0.4,color=red}>"));
        lsdContext.addFact("Something to highlight", "Lorem");
        lsdContext.addFact("Something else to highlight", "amet");
        lsdContext.addFact("Something else to highlight", "consectetur");
        lsdContext.addFact("Something else to highlight", "Thank you!");
        lsdContext.completeScenario("A Warning scenario", p(
                text("A popup with a long text that needs scrolling: "),
                a().withHref("#" + "kljasdlfj").withText("click me!"),
                PopupContent.popupDiv("kljasdlfj", "I am popup", ".. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        ).render(), WARN);

        lsdContext.capture("a request from Beta to Gamma", "Please do something");
        lsdContext.capture("sync a synchronous response from Gamma to Beta", "Some Error (123456)");
        lsdContext.addFact("some important value", "123456");
        lsdContext.completeScenario("An Error scenario", "<p>Failure! Expected value to be 123 but was 123456</p>", ERROR);

        Approvals.verify(lsdContext.completeReport("Approval Report").toFile());
    }

    @Test
    void hasNoDiagramSectionWhenThereAreNoEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);
        lsdContext.addFact("Fact", "no diagrams");
        lsdContext.completeScenario("A scenario without events", "This has no diagrams", SUCCESS);
        
        Approvals.verify(lsdContext.completeReport("No Diagrams Report").toFile());
    }

    @Test
    void createsIndex() {
        lsdContext.completeScenario("Scenario 1", "Error", ERROR);
        lsdContext.completeScenario("Scenario 2", "Warn", WARN);
        lsdContext.completeScenario("Scenario 3", "Success", SUCCESS);
        lsdContext.completeReport("First Report (Error)");

        lsdContext.completeScenario("Scenario 1", "Warn", WARN);
        lsdContext.completeScenario("Scenario 2", "Success", SUCCESS);
        lsdContext.completeReport("Second Report (Warn)");

        lsdContext.completeScenario("Scenario 1", "Success", SUCCESS);
        lsdContext.completeReport("Third Report");

        lsdContext.completeReport("Fourth Report");

        Approvals.verify(lsdContext.createIndex().toFile());
    }

    @Test
    void generateReport() {
        Writer writer = new StringWriter();

        String result = lsdContext.generateReport("Some title", writer);

        Approvals.verify(result);
    }
    private String nextId() {
        return idGenerator.next();
    }
}