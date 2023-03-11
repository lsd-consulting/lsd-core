package com.lsd.events

enum class ArrowType(private val markup: String) {
    SOLID("-%s>"), 
    BI_DIRECTIONAL("<-%s>"), 
    BI_DIRECTIONAL_DOTTED("<-%s->"), 
    LOST("-%s>x"), 
    DOTTED("--%s>"), 
    DOTTED_THIN("--%s>>");

    fun toMarkup(colour: String): String {
        val colourMarkup = if (colour.isBlank()) "" else "[#$colour]"
        return String.format(markup, colourMarkup)
    }
}