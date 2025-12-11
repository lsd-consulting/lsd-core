package com.lsd.core.builders

import com.lsd.core.domain.SequenceEvent

interface SequenceEventBuilder {
    fun build(): SequenceEvent
}