package com.lsd.core.builders

import com.lsd.core.IdGenerator
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.MessageType.SYNCHRONOUS_RESPONSE
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import java.time.Duration
import java.time.Instant
import kotlin.time.toJavaDuration

private val idGenerator = IdGenerator(isDeterministic = false)

class MessageBuilder {
    private var id: String? = null
    private var from: Participant? = null
    private var to: Participant? = null
    private var label: String? = null
    private var data: Any? = null
    private var colour: String? = null
    private var duration: Duration? = null
    private var created: Instant? = null
    private var type: MessageType? = null

    fun id(id: String) = apply { this.id = id }
    fun from(from: String) = apply { from(PARTICIPANT.called(from)) }
    fun from(from: Participant) = apply { this.from = from }
    fun to(to: String) = apply { to(PARTICIPANT.called(to)) }
    fun to(to: Participant) = apply { this.to = to }
    fun label(label: String) = apply { this.label = label }
    fun data(data: Any) = apply { this.data = data }
    fun colour(colour: String) = apply { this.colour = colour }
    fun type(type: MessageType) = apply { this.type = type }
    fun duration(duration: Duration) = apply { this.duration = duration }
    fun duration(duration: kotlin.time.Duration) = apply { duration(duration.toJavaDuration()) }
    fun created(instant: Instant) = apply { this.created = instant }
    fun build() = Message(
        id = id ?: idGenerator.next(),
        from = from ?: PARTICIPANT.called(""),
        to = to ?: PARTICIPANT.called(""),
        label = label ?: "",
        data = data,
        colour = colour ?: "",
        duration = duration,
        created = created ?: Instant.now(),
        type = type ?: MessageType.SYNCHRONOUS
    )

    companion object {
        @JvmStatic
        fun messageBuilder(): MessageBuilder = MessageBuilder()
    }
}

/**
 * Convenience functions for Kotlin DSL style.
 */
infix fun Pair<Any, Any>.message(config: MessageBuilder.() -> Unit): Message =
    MessageBuilder()
        .from(first.toParticipant())
        .to(second.toParticipant())
        .apply(config)
        .build()

infix fun Pair<Any, Any>.message(label: String): Message = message { label(label) }
infix fun Pair<Any, Any>.message(duration: kotlin.time.Duration): Message = message { duration(duration) }
infix fun Pair<Any, Any>.reply(config: MessageBuilder.() -> Unit): Message = message {
    type(SYNCHRONOUS_RESPONSE)
    apply(config)
}

infix fun Pair<Any, Any>.reply(label: String): Message = reply { label(label) }
infix fun Pair<Any, Any>.reply(duration: kotlin.time.Duration): Message = reply { duration(duration) }

private fun Any.toParticipant(): Participant = when (this) {
    is Participant -> this
    is String -> PARTICIPANT.called(this)
    else -> throw UnsupportedOperationException("Only Participant or String types are currently supported.")
}
