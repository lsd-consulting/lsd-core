package com.lsd;

import com.lsd.diagram.ComponentDiagramGenerator;
import com.lsd.diagram.SequenceDiagramGenerator;
import com.lsd.events.SequenceEvent;
import com.lsd.parse.MessageParser;
import com.lsd.parse.Parser;
import com.lsd.parse.SynchronousResponseParser;
import com.lsd.properties.LsdProperties;
import com.lsd.report.HtmlIndexWriter;
import com.lsd.report.HtmlReportRenderer;
import com.lsd.report.HtmlReportWriter;
import com.lsd.report.model.DataHolder;
import com.lsd.report.model.Participant;
import com.lsd.report.model.Report;
import com.lsd.report.model.Scenario;

import java.nio.file.Path;
import java.util.*;

import static com.lsd.properties.LsdProperties.DETERMINISTIC_IDS;
import static com.lsd.properties.LsdProperties.MAX_EVENTS_PER_DIAGRAM;
import static java.util.stream.Collectors.toList;

public class LsdContext {

    private static final LsdContext INSTANCE = new LsdContext();

    private final List<CapturedScenario> capturedScenarios = new ArrayList<>();
    private final List<CapturedReport> capturedReports = new ArrayList<>();
    private final List<Participant> participants = new ArrayList<>();
    private final Set<String> includes = new LinkedHashSet<>();
    private final IdGenerator idGenerator = new IdGenerator(LsdProperties.getBoolean(DETERMINISTIC_IDS));
    private final List<Parser> parsers = parsers();
    private final HtmlReportWriter htmlReportWriter = new HtmlReportWriter(new HtmlReportRenderer());
    private final int maxEventsPerDiagram = LsdProperties.getInt(MAX_EVENTS_PER_DIAGRAM);

    private CapturedScenario currentScenario = new CapturedScenario();

    /**
     * Convenience method to create a singleton instance.
     * If you prefer to use other means to share a single instance or
     * want to create multiple instances then use the default constructor instead.
     *
     * @return A singleton instance of LsdContext
     */
    public static LsdContext getInstance() {
        return INSTANCE;
    }

    public void addParticipants(List<Participant> additionalParticipants) {
        participants.addAll(additionalParticipants);
    }

    public void includeFiles(Set<String> additionalIncludes) {
        includes.addAll(additionalIncludes);
    }

    public void addFact(String key, String value) {
        currentScenario.addFact(key, value);
    }

    public void capture(SequenceEvent event) {
        currentScenario.add(event);
    }

    /**
     * Allow string representations of events to be interpreted. If none of the parsers match the pattern then nothing will be captured
     *
     * @param pattern The input string that represents some event to be captured on the sequence diagram
     * @param body    The extra data associated with the event (may be null).
     */
    public void capture(String pattern, String body) {
        var event = parsers.stream()
                .map(parser -> parser.parse(pattern, body))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        event.ifPresent(this::capture);
    }

    public void completeScenario(String title, String description, OutcomeStatus status) {
        currentScenario.setTitle(title);
        currentScenario.setDescription(description);
        currentScenario.setOutcomeStatus(status);
        capturedScenarios.add(currentScenario);
        currentScenario = new CapturedScenario();
    }

    public Path completeReport(String title) {
        Report report = buildReport(title);
        Path path = htmlReportWriter.writeToFile(report);
        capturedReports.add(new CapturedReport(report.getTitle(), path.getFileName().toString(), report.getStatus()));
        capturedScenarios.clear();
        currentScenario = new CapturedScenario();
        return path;
    }

    public String generateReport(String title) {
        Report report = buildReport(title);
        return htmlReportWriter.writeToString(report);
    }

    public Path createIndex() {
        return HtmlIndexWriter.writeToFile(capturedReports);
    }

    public void clear() {
        idGenerator.reset();
        capturedScenarios.clear();
        capturedReports.clear();
        currentScenario = new CapturedScenario();
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    private Report buildReport(String title) {
        return Report.builder()
                .title(title)
                .status(determineOverallStatus(capturedScenarios))
                .scenarios(capturedScenarios.stream()
                        .map(capturedScenario -> Scenario.builder()
                                .title(capturedScenario.getTitle())
                                .id(idGenerator.next())
                                .status(capturedScenario.getOutcomeStatus().getCssClass())
                                .description(capturedScenario.getDescription())
                                .facts(capturedScenario.getFacts())
                                .dataHolders(capturedScenario.getSequenceEvents().stream()
                                        .filter(DataHolder.class::isInstance)
                                        .map(DataHolder.class::cast)
                                        .collect(toList()))
                                .sequenceDiagram(SequenceDiagramGenerator.builder()
                                        .idGenerator(idGenerator)
                                        .events(capturedScenario.getSequenceEvents())
                                        .participants(participants)
                                        .includes(includes)
                                        .build()
                                        .diagram(maxEventsPerDiagram)
                                        .orElse(null))
                                .componentDiagram(ComponentDiagramGenerator.builder()
                                        .idGenerator(idGenerator)
                                        .events(capturedScenario.getSequenceEvents())
                                        .participants(participants)
                                        .build()
                                        .diagram()
                                        .orElse(null))
                                .build())
                        .collect(toList()))
                .build();
    }

    private String determineOverallStatus(List<CapturedScenario> capturedScenarios) {
        return capturedScenarios.stream()
                .map(CapturedScenario::getOutcomeStatus)
                .sorted()
                .findFirst()
                .map(OutcomeStatus::getCssClass)
                .orElse("");
    }

    private List<Parser> parsers() {
        return List.of(
                new MessageParser(idGenerator),
                new SynchronousResponseParser(idGenerator)
        );
    }
}
