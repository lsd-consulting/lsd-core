package com.lsd.core.report.approval;

import com.lsd.core.IdGenerator;
import com.lsd.core.LsdContext;
import com.lsd.core.domain.*;
import com.lsd.core.report.PopupContent;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.core.builders.MessageBuilder.messageBuilder;
import static com.lsd.core.domain.MessageType.*;
import static com.lsd.core.domain.ParticipantType.*;
import static com.lsd.core.domain.Status.*;
import static j2html.TagCreator.*;

class LsdContextTest {

    private final LsdContext lsdContext = LsdContext.getInstance();

    private final IdGenerator idGenerator = lsdContext.getIdGenerator();

    private final LinkedHashSet<String> additionalIncludes = new LinkedHashSet<>(List.of(
            "tupadr3/font-awesome-5/hamburger",
            "tupadr3/font-awesome-5/heart"
    ));

    private final Participant arnie = ACTOR.called("A", "Arnie");
    private final Participant unused = BOUNDARY.called("Unused participant");
    private final Participant badboy = DATABASE.called("B", "Badboy\\nDB");

    private final List<Participant> participants = List.of(arnie, unused, badboy);

    @BeforeEach
    public void clearContext() {
        lsdContext.clear();
    }

    @Test
    void createsReportWithScenariosAndEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);

        lsdContext.capture(messageBuilder().id(nextId()).to("A").label("in").data("start some job").type(SHORT_INBOUND).build());
        lsdContext.capture(messageBuilder().id(nextId()).from("A").to("B").label("Message 1").data("some data 1").type(BI_DIRECTIONAL).build());
        lsdContext.capture(messageBuilder().id(nextId()).from("B").label("out").data("some data 1").type(SHORT_OUTBOUND).build());
        lsdContext.capture(messageBuilder().id(nextId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").type(LOST).build());
        lsdContext.capture(messageBuilder().id(nextId()).label("A synchronous response").from("Gamma").to("Beta").data("200 OK").type(SYNCHRONOUS_RESPONSE).build());
        lsdContext.completeScenario("A Success scenario", "Given a first scenario description<br/>When something happens<br/>Then something else happens", SUCCESS);

        lsdContext.capture(messageBuilder().id(nextId()).label("Sending food <$hamburger{scale=0.4}>").from("A").to("B").colour("orange").type(SYNCHRONOUS_RESPONSE).build());
        lsdContext.capture(new TimeDelay(null));
        lsdContext.capture("Sending a response from B to A [#red]", "Thank You!");
        lsdContext.capture(new NoteLeft("Friends <$heart{scale=0.4,color=red}>", null));
        lsdContext.addFact("Something to highlight", "Lorem");
        lsdContext.addFact("Something else to highlight", "amet");
        lsdContext.addFact("Something else to highlight", "consectetur");
        lsdContext.addFact("Something else to highlight", "Thank you!");
        lsdContext.completeScenario("A Warning scenario", p(
                text("A popup with a long text that needs scrolling: "),
                a().withHref("#" + "kljasdlfj").withText("click me!"),
                PopupContent.popupDiv("kljasdlfj", "I am popup", ".. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        ).render(), FAILURE);

        lsdContext.capture("a request from Beta to Gamma", "Please do something");
        lsdContext.capture("sync a synchronous response from Gamma to Beta", "Some Error (123456)");
        lsdContext.addFact("some important value", "123456");
        lsdContext.completeScenario("An Error scenario", "<p>Failure! Expected value to be 123 but was 123456</p>", ERROR);

        Approvals.verifyHtml(lsdContext.generateReport("Approval Report"));
    }

    @Test
    void diagramIsSplitOnNewPageEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);

        lsdContext.capture(new PageTitle("A title"));
        lsdContext.capture(messageBuilder().id(nextId()).to("A").label("in").data("start some job").type(SHORT_INBOUND).build());
        lsdContext.capture(new Newpage(new PageTitle("A second page")));
        lsdContext.capture(messageBuilder().id(nextId()).from("B").label("out").data("some data 1").type(SHORT_OUTBOUND).build());
        lsdContext.capture(new Newpage(new PageTitle("A third page")));
        lsdContext.capture(messageBuilder().id(nextId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").type(LOST).build());
        lsdContext.completeScenario("A Success scenario", "Given a first scenario description<br/>When something happens<br/>Then something else happens", SUCCESS);

        Approvals.verifyHtml(lsdContext.generateReport("Split by NewPage Report"));
    }
    
    @Test
    void notesCanBeAddedToParticipants() {
        var arnie = ACTOR.called("Arnie");
        var bettie = BOUNDARY.called("Bettie");
        var cat = CONTROL.called("Cat");

        lsdContext.addParticipants(List.of(arnie, bettie, cat));

        lsdContext.capture(new PageTitle("Adding notes"));
        lsdContext.capture(messageBuilder().id(nextId()).from(arnie).to(bettie).label("Good day to you!").build());
        lsdContext.capture(new NoteLeft("Left note", null));
        lsdContext.capture(new NoteRight("Right note", null));
        lsdContext.capture(new TimeDelay("a few seconds later"));
        lsdContext.capture(messageBuilder().id(nextId()).from(bettie).to(cat).label("Good day to you!").build());
        lsdContext.capture(new NoteLeft("Left of Cat", cat));
        lsdContext.capture(new NoteRight("Right of Cat", cat));
        lsdContext.capture(new NoteRight("Right note", null));
        lsdContext.capture(new LogicalDivider("a divider"));
        lsdContext.capture(messageBuilder().id(nextId()).from(cat).to(arnie).label("Me?").build());
        lsdContext.capture(new NoteRight("Right note", null));
        lsdContext.capture(new VerticalSpace(20));
        lsdContext.capture(new NoteLeft("Left of Bettie", bettie));
        lsdContext.capture(new NoteRight("Right of Bettie", bettie));
        
        lsdContext.completeScenario("A Success scenario", "description", SUCCESS);

        Approvals.verifyHtml(lsdContext.generateReport("Adding notes to participants"));
    }

    @Test
    void hasNoDiagramSectionWhenThereAreNoEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);
        lsdContext.addFact("Fact", "no diagrams");
        lsdContext.completeScenario("A scenario without events", "This has no diagrams", SUCCESS);

        Approvals.verifyHtml(lsdContext.generateReport("No Diagrams Report"));
    }

    @Test
    void hasNoDiagramSectionWhenEventsAreCleared() {
        lsdContext.addParticipants(participants);
        lsdContext.capture(messageBuilder().id(nextId()).to("A").label("in").data("start some job").type(SHORT_INBOUND).build());
        lsdContext.capture(messageBuilder().id(nextId()).to("B").label("in").data("start another job").type(SHORT_INBOUND).build());

        lsdContext.clearScenarioEvents();
        lsdContext.completeScenario("A scenario without events (cleared down)", "This has no diagrams", SUCCESS);

        Approvals.verifyHtml(lsdContext.generateReport("No Diagrams Report (cleared down)"));
    }

    @Test
    void createsIndex() {
        lsdContext.completeScenario("Scenario 1", "Error", ERROR);
        lsdContext.completeScenario("Scenario 2", "Warn", FAILURE);
        lsdContext.completeScenario("Scenario 3", "Success", SUCCESS);
        lsdContext.completeReport("First Report (Error)");

        lsdContext.completeScenario("Scenario 1", "Warn", FAILURE);
        lsdContext.completeScenario("Scenario 2", "Success", SUCCESS);
        lsdContext.completeReport("Second Report (Warn)");

        lsdContext.completeScenario("Scenario 1", "Success", SUCCESS);
        lsdContext.completeReport("Third Report");

        lsdContext.completeReport("Fourth Report");

        Approvals.verify(lsdContext.createIndex().toFile());
    }

    @Test
    void generateReport() {
        String result = lsdContext.generateReport("Some title");

        Approvals.verify(result);
    }

    private String nextId() {
        return idGenerator.next();
    }
}