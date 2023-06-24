package com.lsd.core.report

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import com.lsd.core.report.model.Report

class HtmlReportRenderer : ReportRenderer {
    private val template = HandlebarsWrapper.compile("templates/html-report")

    override fun render(report: Report, devMode: Boolean): String {
        return template
            .apply(
                Context.newBuilder(
                    mutableMapOf(
                        "report" to report,
                        "devMode" to devMode,
                    )
                ).resolver(
                    MapValueResolver.INSTANCE,
                    JavaBeanValueResolver.INSTANCE,
                    MethodValueResolver.INSTANCE
                ).build()
            )
    }
}