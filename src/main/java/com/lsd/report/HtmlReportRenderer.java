package com.lsd.report;

import com.lsd.report.model.Report;
import com.lsd.report.pebble.LsdPebbleExtension;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class HtmlReportRenderer implements ReportRenderer {

    private final PebbleEngine engine = new PebbleEngine.Builder()
            .extension(new LsdPebbleExtension())
            .build();

    private final PebbleTemplate compiledTemplate = engine.getTemplate("templates/html-report.peb");

    @SneakyThrows
    @Override
    public String render(Report report) {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, Map.of("report", report));
        return writer.toString();
    }

    @SneakyThrows
    @Override
    public String render(Report report, Writer writer) {
        compiledTemplate.evaluate(writer, Map.of("report", report));
        return writer.toString();
    }
}
