package com.lsd.report;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.lsd.report.model.Report;
import lombok.SneakyThrows;

import java.util.Map;

import static com.lsd.Sanitiser.sanitise;


public class HtmlReportRenderer implements ReportRenderer {

    private final Template template = compileTemplate("templates/html-report");

    @SneakyThrows
    @Override
    public String render(Report report) {
        return template
                .apply(Context.newBuilder(Map.of("report", report)).resolver(
                        MapValueResolver.INSTANCE,
                        JavaBeanValueResolver.INSTANCE,
                        FieldValueResolver.INSTANCE,
                        MethodValueResolver.INSTANCE
                ).build());
    }

    @SneakyThrows
    private static Template compileTemplate(String location) {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper("sanitise", (Helper<String>) (input, options) -> sanitise(input));
        return handlebars.compile(location);
    }
}
