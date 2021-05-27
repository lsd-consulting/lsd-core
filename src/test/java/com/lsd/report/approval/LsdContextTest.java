package com.lsd.report.approval;

import com.lsd.LsdContext;
import com.lsd.events.Markup;
import com.lsd.events.Message;
import com.lsd.events.NoteLeft;
import com.lsd.events.ResponseMessage;
import com.lsd.report.model.Participant;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.ParticipantType.*;

class LsdContextTest {

    private final LsdContext lsdContext = LsdContext.getInstance();

    private LinkedHashSet<String> additionalIncludes = new LinkedHashSet<>(List.of(
            "tupadr3/font-awesome-5/hamburger",
            "tupadr3/font-awesome-5/heart"
    ));
    
    private final List<Participant> participants = List.of(
            ACTOR.called("A", "Arnie"),
            BOUNDARY.called("Unused participant"),
            DATABASE.called("B", "Badboy\\nDB")
    );

    private int counter = 0;

    @BeforeEach
    public void clearContext() {
        lsdContext.clear();
    }

    @Test
    void createsReportWithScenariosAndEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);

        lsdContext.capture(Message.builder().id(incrementId()).from("A").to("B").label("Message 1").data("some data 1").build());
        lsdContext.capture(Message.builder().id(incrementId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").build());
        lsdContext.completeScenario("First scenario", "First scenario description");
       
        lsdContext.capture(Message.builder().id(incrementId()).label("Sending food <$hamburger{scale=0.4}>").from("A").to("B").data("Hi").build());
        lsdContext.capture(new Markup("..."));
        lsdContext.capture(ResponseMessage.builder().id(incrementId()).label("Sending a response").from("B").to("A").data("Thank You!").build());
        lsdContext.capture(new NoteLeft("Friends <$heart{scale=0.4}>"));
        lsdContext.completeScenario("Second scenario", "Second scenario description");

        Approvals.verify(lsdContext.completeReport("Report 1").toFile());
    }

    private String incrementId() {
        return String.valueOf(counter++);
    }
}