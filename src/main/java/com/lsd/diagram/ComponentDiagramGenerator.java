package com.lsd.diagram;

import com.lsd.IdGenerator;
import com.lsd.events.Message;
import com.lsd.events.SequenceEvent;
import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Diagram;
import com.lsd.report.model.Participant;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.Builder;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Builder
public class ComponentDiagramGenerator {
    private final PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/component-uml.peb");

    private final List<Participant> participants;
    private final List<SequenceEvent> events;
    private final IdGenerator idGenerator;

    public Diagram diagram() {
        String uml = generateUml();
        String svg = generateSvg(uml);
        return Diagram.builder()
                .id(idGenerator.next())
                .uml(uml)
                .svg(svg)
                .build();
    }

    private String generateSvg(String markup) {
        SvgConverter svgConverter = new SvgConverter();

        return svgConverter.convert(markup);
    }

    @SneakyThrows
    private String generateUml() {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of(
                "theme", LsdProperties.get(DIAGRAM_THEME),
                "participants", participants.stream()
                        .map(Participant::getMarkup)
                        .map(markup -> markup.replaceAll("participant", "component"))
                        .collect(toList()),
                "events", events.stream()
                        .filter(event -> event instanceof Message)
                        .map(event -> (Message) event)
                        .filter(not(message -> message.getLabel().contains("response"))) //TODO find better solution to remove synchronous resposes
                        .map(message -> String.format("[%s] -> %s", message.getFrom(), message.getTo()))
                        .collect(toList())
        ));
        return writer.toString();
    }
}
