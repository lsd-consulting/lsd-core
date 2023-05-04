package com.lsd.core.domain

sealed interface SequenceEvent

data class NoteLeft @JvmOverloads constructor(val note: String, val ofParticipant: Participant? = null) : SequenceEvent
data class NoteRight @JvmOverloads constructor(val note: String, val ofParticipant: Participant? = null) : SequenceEvent
data class NoteOver(val note: String, val participant: Participant) : SequenceEvent
data class PageTitle(val title: String) : SequenceEvent
data class Newpage(val pageTitle: PageTitle) : SequenceEvent
data class VerticalSpace(val size: Int? = null) : SequenceEvent
data class LogicalDivider(val label: String) : SequenceEvent
data class TimeDelay(val label: String? = null) : SequenceEvent
data class ActivateLifeline @JvmOverloads constructor(val participant: Participant, var colour: String? = null) : SequenceEvent
data class DeactivateLifeline(val participant: Participant) : SequenceEvent

data class Message
@JvmOverloads constructor(
    val id: String,
    val from: Participant,
    val to: Participant,
    val label: String = "",
    val type: MessageType = MessageType.SYNCHRONOUS,
    val colour: String = "",
    val data: Any? = null,
) : SequenceEvent

enum class MessageType {
    ASYNCHRONOUS,
    BI_DIRECTIONAL,
    LOST,
    SHORT_INBOUND,
    SHORT_OUTBOUND,
    SYNCHRONOUS,
    SYNCHRONOUS_RESPONSE,
}
