package com.lsd.events

interface SequenceEvent {
    fun toMarkup(): String
}

fun List<SequenceEvent>.groupedByPages() =
    fold(mutableListOf<MutableList<SequenceEvent>>()
        .also { it.add(ArrayList()) }) { groups, event ->
        when (event) {
            is Newpage -> groups.apply {
                add(ArrayList())
                last().add(event.pageTitle)
            }

            else -> groups.apply { last().add(event) }
        }
    }