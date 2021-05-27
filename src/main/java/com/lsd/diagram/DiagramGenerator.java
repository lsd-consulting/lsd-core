package com.lsd.diagram;

import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Participant;
import com.lsd.report.model.SequenceDiagram;
import com.lsd.events.SequenceEvent;
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
    private final List<SequenceEvent> events;

    public SequenceDiagram sequenceDiagram() {
        String uml = generateSequenceUml();
        String svg = generateSequenceSvg(uml);
        return SequenceDiagram.builder()
                .uml(uml)
                .svg(svg)
                .build();
    }

    private String generateSequenceSvg(String markup) {
        SvgConverter svgConverter = new SvgConverter();

        return svgConverter.convert(markup);
    }

    @SneakyThrows
    private String generateSequenceUml() {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of(
                "theme", LsdProperties.get(DIAGRAM_THEME),
                "includes", includes,
                "participants", participants,
                "events", events.stream()
                        .map(SequenceEvent::toMarkup)
                        .collect(toList())
        ));
        return writer.toString();
    }
}
