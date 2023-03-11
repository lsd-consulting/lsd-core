package com.lsd.report

import com.github.jknack.handlebars.Handlebars
import com.lsd.CapturedReport

class HtmlIndexRenderer {
    private val template = Handlebars().compile("templates/html-index")

    fun render(capturedReports: List<CapturedReport>): String =
        template.apply(mapOf("capturedReports" to capturedReports))
}