package com.lsd.diagram;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.lsd.IdGenerator;
import com.lsd.events.Message;
import com.lsd.events.SequenceEvent;
import com.lsd.events.SynchronousResponse;
import com.lsd.properties.LsdProperties;
import com.lsd.report.model.Diagram;
import com.lsd.report.model.Participant;
import lombok.Builder;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.lsd.properties.LsdProperties.DIAGRAM_THEME;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@Builder
public class ComponentDiagramGenerator {
    private final Handlebars handlebars = new Handlebars();
    private final Template template = compileTemplate("templates/component-uml");
    private final SvgConverter svgConverter = new SvgConverter();

    private final List<Participant> participants;
    private final List<SequenceEvent> events;
    private final IdGenerator idGenerator;

    public Optional<Diagram> diagram() {
        if (events.isEmpty()) {
            return Optional.empty();
        }
        var uml = generateUml();
        return Optional.of(Diagram.builder()
                .id(idGenerator.next())
                .uml(uml)
                .svg(generateSvg(uml))
                .build());
    }

    private String generateSvg(String markup) {
        return svgConverter.convert(markup);
    }

    @SneakyThrows
    private String generateUml() {
        return template.apply(Map.of(
                "theme", LsdProperties.get(DIAGRAM_THEME),
                "participants", participants.stream()
                        .map(Participant::getMarkup)
                        .map(markup -> markup.replaceAll("^participant ", "component "))
                        .distinct()
                        .collect(toList()),
                "events", events.stream()
                        .filter(event -> event instanceof Message)
                        .filter(not(SynchronousResponse.class::isInstance))
                        .map(event -> (Message) event)
                        .map(message -> String.format("[%s] --> %s", ValidComponentName.of(message.getFrom()), ValidComponentName.of(message.getTo())))
                        .distinct()
                        .collect(toList())
        ));
    }
    @SneakyThrows
    private static Template compileTemplate(String location) {
        return new Handlebars().compile(location);
    }
}
