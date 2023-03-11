package com.lsd.events

import com.lsd.builders.SynchronousResponseBuilder
import com.lsd.events.ArrowType.DOTTED_THIN

/**
 * Represents a synchronous response message (e.g. A HTTP response to a request)
 * <p>
 * This type of message will get filtered out when generating component diagrams since the response is an
 * indirect message to the recipient.
 */
data class SynchronousResponse(
    override val id: String,
    override val from: String,
    override val to: String,
    override val label: String,
    override val colour: String = "",
    override val data: Any? = null,
    override val arrowType: ArrowType = DOTTED_THIN,
) : SequenceMessage {
    companion object {
        @JvmStatic
        fun builder(): SynchronousResponseBuilder = SynchronousResponseBuilder()
    }
} 