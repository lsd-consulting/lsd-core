package com.lsd.core.builders

import com.lsd.core.IdGenerator
import com.lsd.core.domain.Message
import com.lsd.core.domain.MessageType
import com.lsd.core.domain.Participant
import com.lsd.core.domain.ParticipantType.PARTICIPANT
import java.time.Duration
import java.time.Instant
import kotlin.time.toJavaDuration

private val idGenerator = IdGenerator(isDeterministic = false)

class MessageBuilder : SequenceEventBuilder {
    private var id: String? = null
    private var from: Participant = PARTICIPANT.called("")
    private var to: Participant = PARTICIPANT.called("")
    private var label: String = ""
    private var data: Any? = null
    private var colour: String = ""
    private var duration: Duration? = null
    private var created: Instant = Instant.now()
    private var type: MessageType = MessageType.SYNCHRONOUS

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

    override fun build() = Message(
        id = id ?: idGenerator.next(),
        from = from,
        to = to,
        label = label,
        data = data,
        colour = colour,
        duration = duration,
        created = created,
        type = type
    )

    companion object {
        @JvmStatic
        fun messageBuilder(): MessageBuilder = MessageBuilder()
    }
}

/**
 * Convenience functions for Kotlin DSL style.
 */
infix fun MessageBuilder.withLabel(label: String): MessageBuilder = label(label)
infix fun MessageBuilder.withData(data: Any): MessageBuilder = data(data)
infix fun MessageBuilder.withDuration(duration: kotlin.time.Duration): MessageBuilder = duration(duration)
infix fun MessageBuilder.withType(type: MessageType): MessageBuilder = type(type)
infix fun MessageBuilder.withColour(colour: String): MessageBuilder = colour(colour)
infix fun MessageBuilder.with(config: MessageBuilder.() -> Unit = {}): MessageBuilder = apply(config)

infix fun Participant.messages(other: Participant): MessageBuilder = MessageBuilder().from(this).to(other)
infix fun Participant.messages(other: String): MessageBuilder = this.messages(other.toParticipant())
infix fun String.messages(other: String): MessageBuilder = this.toParticipant().messages(other.toParticipant())
infix fun String.messages(other: Participant): MessageBuilder = this.toParticipant().messages(other)

fun String.toParticipant(): Participant = PARTICIPANT.called(this)
