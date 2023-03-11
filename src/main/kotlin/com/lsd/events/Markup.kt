package com.lsd.events

class Markup(val value: String) : SequenceEvent {
    override fun toMarkup(): String {
        return value
    }
}