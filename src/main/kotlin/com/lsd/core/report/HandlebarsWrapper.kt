package com.lsd.core.report

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import com.lsd.core.sanitise

object HandlebarsWrapper {
    fun compile(template: String): Template {
        return handlebars.compile(template)
    }

    private val handlebars = Handlebars()

    init {
        handlebars.registerHelper<String>("sanitise") { input, _ -> input.sanitise() }
        handlebars.registerHelper<String>("inlineResourceFile") { input, _ ->
            input?.let {
                javaClass.classLoader.getResource(it)?.readText()
            }
        }
    }
}