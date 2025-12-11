package com.lsd.core.domain

import java.time.Duration
import java.time.Instant
import java.time.Instant.now

sealed class SequenceEvent(open val created: Instant = now())

data class NoteLeft
@JvmOverloads constructor(
    val note: String, val ofParticipant: Participant? = null, override val created: Instant = now()
) : SequenceEvent()

data class NoteRight
@JvmOverloads constructor(
    val note: String, val ofParticipant: Participant? = null, override val created: Instant = now()
) : SequenceEvent()

data class NoteOver
@JvmOverloads constructor(
    val note: String, val participant: Participant, override val created: Instant = now()
) : SequenceEvent()

data class PageTitle
@JvmOverloads constructor(
    val title: String, override val created: Instant = now()
) : SequenceEvent()

data class Newpage
@JvmOverloads constructor(
    val pageTitle: PageTitle, override val created: Instant = now()
) : SequenceEvent() {
    companion object {
        infix fun Companion.title(title: String): Newpage = Newpage(PageTitle(title))
    }
}

data class VerticalSpace
@JvmOverloads constructor(
    val size: Int? = null, override val created: Instant = now()
) : SequenceEvent()

data class LogicalDivider
@JvmOverloads constructor(
    val label: String, override val created: Instant = now()
) : SequenceEvent()

data class TimeDelay
@JvmOverloads constructor(
    val label: String? = null, override val created: Instant = now()
) : SequenceEvent()

data class ActivateLifeline
@JvmOverloads constructor(
    val participant: Participant, var colour: String? = null, override val created: Instant = now()
) : SequenceEvent()

data class DeactivateLifeline
@JvmOverloads constructor(
    val participant: Participant, override val created: Instant = now()
) : SequenceEvent()

data class Message
@JvmOverloads constructor(
    val id: String,
    val from: Participant,
    val to: Participant,
    val label: String = "",
    val type: MessageType = MessageType.SYNCHRONOUS,
    val colour: String = "",
    val data: Any? = null,
    val duration: Duration? = null,
    override val created: Instant = now()
) : SequenceEvent()

enum class MessageType {
    ASYNCHRONOUS,
    BI_DIRECTIONAL,
    LOST,
    SHORT_INBOUND,
    SHORT_OUTBOUND,
    SYNCHRONOUS,
    SYNCHRONOUS_RESPONSE,
}
