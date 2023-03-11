package com.lsd.report

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import com.lsd.report.model.Report
import com.lsd.sanitise

class HtmlReportRenderer : ReportRenderer {
    private val template = compileTemplate("templates/html-report")

    override fun render(report: Report): String {
        return template
            .apply(
                Context.newBuilder(mapOf("report" to report)).resolver(
                    MapValueResolver.INSTANCE,
                    JavaBeanValueResolver.INSTANCE,
                    FieldValueResolver.INSTANCE,
                    MethodValueResolver.INSTANCE
                ).build()
            )
    }

    companion object {
        private fun compileTemplate(location: String): Template {
            val handlebars = Handlebars()
            handlebars.registerHelper("sanitise") { input: String?, _ -> sanitise(input) }
            return handlebars.compile(location)
        }
    }
}