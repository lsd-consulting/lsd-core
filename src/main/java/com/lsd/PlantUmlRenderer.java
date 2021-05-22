package com.lsd;

import com.lsd.domain.interactions.Interaction;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class PlantUmlRenderer {
    private final Map<String, Object> model = new HashMap<>();
    private final PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/plant-uml.peb");

    @SneakyThrows
    public String render(Collection<Interaction> interactions) {
        model.put("includes", emptyList()); //TODO
        model.put("participants", emptyList()); //TODO
        model.put("interactions", interactions.stream()
                .map(Interaction::toMarkup)
                .collect(toList()));

        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, model);
        return writer.toString();
    }
}
