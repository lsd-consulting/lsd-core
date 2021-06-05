package com.lsd.report;

import com.lsd.CapturedReport;
import com.lsd.report.pebble.LsdPebbleExtension;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class HtmlIndexRenderer {

    private final PebbleEngine engine = new PebbleEngine.Builder()
            .extension(new LsdPebbleExtension())
            .build();

    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/html-index.peb");

    @SneakyThrows
    public String render(List<CapturedReport> capturedReports) {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of("capturedReports", capturedReports));
        return writer.toString();
    }
}
