package com.lsd.core.report

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars

class ComponentReportRenderer {
    private val template = Handlebars().compile("templates/components-report")

    fun render(model: Model): String {
        return template.apply(
            Context.newBuilder(mapOf("model" to model)).build()
        )
    }
}

data class Model(val title: String, val uml: String, val svg: String)
