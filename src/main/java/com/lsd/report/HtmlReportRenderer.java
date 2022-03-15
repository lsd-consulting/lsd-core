package com.lsd.report;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.lsd.report.model.Report;
import com.lsd.report.pebble.LsdPebbleExtension;
import com.mitchellbosecke.pebble.PebbleEngine;
import lombok.SneakyThrows;

import java.util.Map;


public class HtmlReportRenderer implements ReportRenderer {

    private final PebbleEngine engine = new PebbleEngine.Builder()
            .extension(new LsdPebbleExtension())
            .build();

    private final Template template = compileTemplate("templates/html-report");

    @SneakyThrows
    @Override
    public String render(Report report) {

//        String uml = report.getScenarios().iterator().next().getSequenceDiagram().getUml();

        Context context = Context.newBuilder(Map.of("report", report))
                .resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE, MethodValueResolver.INSTANCE)
                .build();
        return template.apply(context);
    }

    @SneakyThrows
    private static Template compileTemplate(String location) {

        Handlebars handlebars = new Handlebars();
//        handlebars.registerHelper("loud", (Helper<String>) (blog, options) -> options.fn(blog));

        return handlebars.compile(location);
    }
}
