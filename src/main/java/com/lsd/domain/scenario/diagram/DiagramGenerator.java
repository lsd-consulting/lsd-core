package com.lsd.domain.scenario.diagram;

import com.lsd.domain.Participant;
import com.lsd.domain.scenario.events.Event;
import com.lsd.domain.scenario.events.Interaction;
import com.lsd.properties.LsdProperties;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.Builder;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static java.util.stream.Collectors.toList;

@Builder
public class DiagramGenerator {
    private final PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/sequence-uml.peb");

    private final Set<String> includes;
    private final List<Participant> participants;
    private final List<Event> events;

    public SequenceDiagram sequenceDiagram() {
        return SequenceDiagram.builder()
                .uml(generateSequenceUml())
                .interactions(events.stream()
                        .filter(Interaction.class::isInstance)
                        .map(Interaction.class::cast)
                        .collect(toList()))
                .build();
    }

    @SneakyThrows
    private String generateSequenceUml() {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of(
                "theme", LsdProperties.get(DIAGRAM_THEME),
                "includes", includes,
                "participants", participants,
                "events", events.stream()
                        .map(Event::toMarkup)
                        .collect(toList())
        ));
        return writer.toString();
    }
}
