package com.lsd.core.adapter.parse

import com.lsd.core.domain.SequenceEvent

interface Parser {
    fun parse(message: String, body: String): SequenceEvent?
}