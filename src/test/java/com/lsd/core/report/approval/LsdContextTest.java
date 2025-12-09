package com.lsd.core.report.approval;

import com.lsd.core.IdGenerator;
import com.lsd.core.LsdContext;
import com.lsd.core.ReportOptionsBuilder;
import com.lsd.core.domain.*;
import com.lsd.core.report.PopupContent;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.core.Scrubber;
import org.approvaltests.scrubbers.RegExScrubber;
import org.approvaltests.scrubbers.Scrubbers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.core.builders.ActivateLifelineBuilder.activation;
import static com.lsd.core.builders.DeactivateLifelineBuilder.deactivation;
import static com.lsd.core.builders.MessageBuilder.messageBuilder;
import static com.lsd.core.domain.MessageType.*;
import static com.lsd.core.domain.ParticipantType.*;
import static com.lsd.core.domain.Status.*;

class LsdContextTest {
    private final LsdContext lsdContext = LsdContext.getInstance();
    private final IdGenerator idGenerator = lsdContext.getIdGenerator();
    private final Participant arnie = ACTOR.called("A", "Arnie");
    private final Participant bettie = BOUNDARY.called("Bettie", null, "red");
    private final Participant cat = CONTROL.called("Cat");

    public LsdContextTest() {
        lsdContext.addParticipants(arnie, bettie, cat);
    }

    private final Scrubber durationScrubber = new RegExScrubber(">\\d+\\.\\d+s<", ">0.00s<");
    private final Scrubber scrubber = Scrubbers.scrubAll(durationScrubber);

    private final LinkedHashSet<String> additionalIncludes = new LinkedHashSet<>(List.of(
            "tupadr3/font-awesome-5/hamburger",
            "tupadr3/font-awesome-5/heart"
    ));

    private final ReportOptionsBuilder reportOptions = new ReportOptionsBuilder()
            .devMode(false)
            .maxEventsPerDiagram(100)
            .metricsEnabled(true);

    @BeforeEach
    public void clearContext() {
        lsdContext.clear();
    }

    @Test
    void createsReportWithScenarios() {
        captureEvents();
        Approvals.verifyHtml(lsdContext.renderReport("Approval Report", reportOptions.build()), new Options(scrubber));
    }

    @Test
    void createsReportWithScenariosInDevMode() {
        captureEvents();
        Approvals.verifyHtml(lsdContext.renderReport(
                "Approval Report", reportOptions.devMode(true).build()
        ), new Options(scrubber));
    }

    private void captureEvents() {
        lsdContext.includeFiles(additionalIncludes);
        scenarioWithMessages();
        scenarioWithLifelineActivation();
        scenarioWithNotes();
        scenarioWithMultipleDiagrams();
        scenarioWithWarningStatus();
        scenarioWithErrorStatus();
    }

    private void scenarioWithErrorStatus() {
        lsdContext.addFact("some error value", "XYZ");
        lsdContext.capture(messageBuilder().id(nextId()).from(arnie).to(bettie).label("error XYZ").data("Found a fault!").type(LOST).colour("red").build());
        lsdContext.completeScenario("An Error scenario", "<p>Failure! Expected value to be 123 but was XYZ</p>", ERROR);
    }

    private void scenarioWithWarningStatus() {
        lsdContext.addFact("Something to highlight", "Lorem");
        lsdContext.capture(
                messageBuilder().id(nextId()).label("Sending food").from(arnie).to(bettie).colour("orange").build(),
                new TimeDelay()
        );
        lsdContext.completeScenario("A Warning scenario", "A popup with a long text that needs scrolling:"
                        + PopupContent.popupHyperlink(
                        "kljasdlfj",
                        "I am popup",
                        "click me please!",
                        ".. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
                , FAILURE);
    }

    private void scenarioWithMessages() {
        lsdContext.capture(
                messageBuilder().id(nextId()).to(arnie).label("in").data("start some job").type(SHORT_INBOUND).build(),
                messageBuilder().id(nextId()).from(arnie).to(bettie).label("Message 1").data("some data 1").type(BI_DIRECTIONAL).duration(Duration.ofSeconds(1)).build(),
                messageBuilder().id(nextId()).from(bettie).label("out").data("some data 1").type(SHORT_OUTBOUND).build(),
                messageBuilder().id(nextId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").type(LOST).build(),
                messageBuilder().id(nextId()).label("A synchronous response").from("Gamma").to("Beta").data("200 OK").type(SYNCHRONOUS_RESPONSE).duration(Duration.ofSeconds(2)).build()
        );
        lsdContext.completeScenario("A Success scenario with messages", "Given a first scenario description<br/>When something happens<br/>Then something else happens", SUCCESS);
    }

    private void scenarioWithLifelineActivation() {
        lsdContext.capture(
                messageBuilder().id(nextId()).from(arnie).to(bettie).label("Good day to you!").build(),
                activation().of(bettie).colour("red").build(),
                messageBuilder().id(nextId()).from(bettie).to(cat).label("Good day to you!").build(),
                deactivation().of(bettie).build(),
                activation().of(cat).colour("blue").build(),
                messageBuilder().id(nextId()).from(cat).to(arnie).label("Me?").build(),
                deactivation().of(cat).build()
        );
        lsdContext.completeScenario("Lifeline activation/deactivation", "Adding lifelines to participants");
    }

    private void scenarioWithMultipleDiagrams() {
        lsdContext.capture(
                new PageTitle("Splitting diagrams with Newpage"),
                messageBuilder().id(nextId()).to(arnie).label("in").data("start some job").type(SHORT_INBOUND).build(),
                new Newpage(new PageTitle("Another diagram")),
                messageBuilder().id(nextId()).from(bettie).label("out").data("some data 1").type(SHORT_OUTBOUND).build()
        );
        lsdContext.completeScenario("A Newpage scenario", "Splitting a diagram into multiple diagrams using Newpage event type");
    }

    private void scenarioWithNotes() {
        lsdContext.capture(
                new PageTitle("Adding notes"),
                new NoteLeft("Friends <$heart{scale=0.4,color=red}>"),
                messageBuilder().id(nextId()).from(arnie).to(bettie).label("Good day to you!").build(),
                new NoteLeft("Left note"),
                new NoteRight("Right note"),
                new TimeDelay("a few seconds later"),
                messageBuilder().id(nextId()).from(bettie).to(cat).label("Good day to you!").build(),
                new NoteLeft("Left of Cat", cat),
                new NoteRight("Right of Cat", cat),
                new NoteOver("Note over Bettie", bettie),
                new NoteRight("Right note"),
                new LogicalDivider("a divider"),
                messageBuilder().id(nextId()).from(cat).to(arnie).label("Me?").build(),
                new NoteRight("Right note"),
                new VerticalSpace(20),
                new NoteLeft("Left of Bettie", bettie),
                new NoteRight("Right of Bettie", bettie)
        );
        lsdContext.completeScenario("Capturing Notes", "Notes can be added to participants");
    }

    private String nextId() {
        return idGenerator.next();
    }
}