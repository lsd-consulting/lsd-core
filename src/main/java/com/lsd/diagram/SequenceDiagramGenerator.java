package com.lsd.diagram;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.lsd.IdGenerator;
import com.lsd.events.SequenceEvent;
import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Diagram;
import com.lsd.report.model.Participant;
import lombok.Builder;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.partition;

@Builder
public class SequenceDiagramGenerator {
    private final Handlebars handlebars = new Handlebars();
    private final Template template = compileTemplate("templates/sequence-uml");

    private final Set<String> includes;
    private final List<Participant> participants;
    private final List<SequenceEvent> events;
    private final IdGenerator idGenerator;

    public Optional<Diagram> diagram(int maxEventsPerDiagram) {
        if (events.isEmpty()) {
            return Optional.empty();
        }
        var uml = events.stream().collect(new GroupByNewPageEvents())
                .stream()
                .flatMap(sequenceEvents -> partition(sequenceEvents, maxEventsPerDiagram).stream())
                .map(this::generateSequenceUml)
                .collect(joining(System.lineSeparator()));

        String svg = generateSequenceSvg(uml);
        return Optional.of(Diagram.builder()
                .id(idGenerator.next())
                .uml(uml)
                .svg(svg)
                .build());
    }

    private String generateSequenceSvg(String markup) {
        SvgConverter svgConverter = new SvgConverter();

        return svgConverter.convert(markup);
    }

    @SneakyThrows
    private String generateSequenceUml(List<SequenceEvent> events) {
        return template.apply(Map.of(
                "theme", LsdProperties.get(DIAGRAM_THEME),
                "includes", includes,
                "participants", participants.stream().distinct().collect(toList()),
                "events", events.stream()
                        .map(SequenceEvent::toMarkup)
                        .collect(toList())
        ));
    }

    @SneakyThrows
    private static Template compileTemplate(String location) {
        return new Handlebars().compile(location);
    }

}
