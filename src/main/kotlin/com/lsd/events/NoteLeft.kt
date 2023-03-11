package com.lsd.events

data class NoteLeft(val note: String) : SequenceEvent {
    override fun toMarkup(): String = "note left: $note"
}