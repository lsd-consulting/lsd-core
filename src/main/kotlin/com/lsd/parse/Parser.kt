package com.lsd.parse

import com.lsd.events.SequenceEvent

interface Parser {
    fun parse(message: String, body: String): SequenceEvent?
}