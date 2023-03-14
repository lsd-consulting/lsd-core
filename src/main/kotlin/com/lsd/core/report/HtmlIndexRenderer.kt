package com.lsd.core.report

import com.github.jknack.handlebars.Handlebars
import com.lsd.core.report.model.ReportFile

class HtmlIndexRenderer {
    private val template = Handlebars().compile("templates/html-index")

    fun render(reportFiles: List<ReportFile>): String =
        template.apply(mapOf("reportFiles" to reportFiles))
}