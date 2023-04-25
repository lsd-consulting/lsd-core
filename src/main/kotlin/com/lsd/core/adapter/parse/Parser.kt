package com.lsd.core.adapter.parse

import com.lsd.core.domain.SequenceEvent

@Deprecated(message = "To be deleted")
interface Parser {
    fun parse(message: String, body: String): SequenceEvent?
}