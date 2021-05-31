package com.lsd;

import com.lsd.diagram.DiagramGenerator;
import com.lsd.report.model.*;
import com.lsd.events.SequenceEvent;
import com.lsd.report.HtmlReportWriter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class LsdContext {

    private static final LsdContext INSTANCE = new LsdContext();

    private final List<CapturedScenario> capturedScenarios = new ArrayList<>();
    private final List<Participant> participants = new ArrayList<>();
    private final Set<String> includes = new LinkedHashSet<>();

    private CapturedScenario currentScenario = new CapturedScenario();

    private LsdContext() {
    }

    public static LsdContext getInstance() {
        return INSTANCE;
    }

    public void addParticipants(List<Participant> additionalParticipants) {
        participants.addAll(additionalParticipants);
    }

    public void includeFiles(Set<String> additionalIncludes) {
        includes.addAll(additionalIncludes);
    }

    public void capture(SequenceEvent event) {
        currentScenario.add(event);
    }

    public void completeScenario(String title, String description) {
        currentScenario.setTitle(title);
        currentScenario.setDescription(description);
        capturedScenarios.add(currentScenario);
        currentScenario = new CapturedScenario();
    }

    public Path completeReport(String title) {
        Path path = HtmlReportWriter.writeToFile(buildReport(title));
        capturedScenarios.clear();
        currentScenario = new CapturedScenario();
        return path;
    }

    public void clear() {
        capturedScenarios.clear();
        currentScenario = new CapturedScenario();
    }

    private Report buildReport(String title) {
        return Report.builder()
                .title(title)
                .scenarios(capturedScenarios.stream()
                        .map(capturedScenario -> Scenario.builder()
                                .title(capturedScenario.getTitle())
                                .description(capturedScenario.getDescription())
                                .dataHolders(capturedScenario.getSequenceEvents().stream()
                                        .filter(DataHolder.class::isInstance)
                                        .map(DataHolder.class::cast)
                                        .collect(toList()))
                                .sequenceDiagram(DiagramGenerator.builder()
                                        .events(capturedScenario.getSequenceEvents())
                                        .participants(participants)
                                        .includes(includes)
                                        .build().sequenceDiagram())
                                .build())
                        .collect(toList()))
                .build();
    }
}
