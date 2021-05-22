package com.lsd;

import com.lsd.domain.Report;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class HtmlReportRenderer {

    private final PebbleEngine engine = new PebbleEngine.Builder().build();
    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/html-report.peb");

    @SneakyThrows
    public String render(Report report) {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of("report", report));
        return writer.toString();
    }
}
