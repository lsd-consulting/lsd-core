package com.lsd.report;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.lsd.CapturedReport;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HtmlIndexRenderer {

    private final Handlebars handlebars = new Handlebars();
    private final Template template = handlebars.compile("templates/html-index");

    public HtmlIndexRenderer() throws IOException {
    }

    @SneakyThrows
    public String render(List<CapturedReport> capturedReports) {
        return template.apply(Map.of("capturedReports", capturedReports));
    }
}
