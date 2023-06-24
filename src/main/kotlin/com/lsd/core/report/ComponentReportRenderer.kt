package com.lsd.core.report

import com.github.jknack.handlebars.Context

class ComponentReportRenderer {
    private val template = HandlebarsWrapper.compile("templates/components-report")

    fun render(model: Model, devMode: Boolean): String {
        return template.apply(
            Context.newBuilder(
                mapOf(
                    "model" to model,
                    "devMode" to devMode
                )
            ).build()
        )
    }
}

data class Model(val title: String, val uml: String, val svg: String)
