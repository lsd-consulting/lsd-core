package com.lsd.core.domain

sealed interface SequenceEvent

data class NoteLeft(val note: String, val ofParticipant: Participant? = null) : SequenceEvent
data class NoteRight(val note: String, val ofParticipant: Participant? = null) : SequenceEvent
data class PageTitle(val title: String) : SequenceEvent
data class Newpage(val pageTitle: PageTitle) : SequenceEvent
data class VerticalSpace(val size: Int?) : SequenceEvent
data class LogicalDivider(val label: String) : SequenceEvent
data class TimeDelay(val label: String?) : SequenceEvent
data class ActivateLifeline(val participant: Participant, var colour: String? = null) : SequenceEvent
data class DeactivateLifeline(val participant: Participant) : SequenceEvent

data class Message(
    val id: String,
    val from: ComponentName,
    val to: ComponentName,
    val label: String,
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
