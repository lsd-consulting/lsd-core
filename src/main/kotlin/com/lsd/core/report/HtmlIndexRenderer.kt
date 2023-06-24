package com.lsd.core.report

import com.lsd.core.report.model.ReportFile

class HtmlIndexRenderer {
    private val template = HandlebarsWrapper.compile("templates/html-index")

    fun render(reportFiles: List<ReportFile>, devMode: Boolean): String =
        template.apply(
            mapOf(
                "reportFiles" to reportFiles,
                "devMode" to devMode
            )
        )
}