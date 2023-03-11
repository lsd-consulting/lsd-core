package com.lsd.events

import com.lsd.builders.MessageBuilder
import com.lsd.events.ArrowType.SOLID

data class Message(
    override val id: String,
    override val label: String,
    override val data: Any? = null,
    override val from: String,
    override val to: String,
    override val colour: String = "",
    override val arrowType: ArrowType = SOLID,
) : SequenceMessage {
    companion object {
        @JvmStatic
        fun builder(): MessageBuilder = MessageBuilder()
    }
}