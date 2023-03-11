package com.lsd.events

import java.lang.System.lineSeparator

/**
 * Used to add a title in a sequence diagram.
 */
data class PageTitle(val title: String) : SequenceEvent {
    override fun toMarkup(): String {
        val safeTitle = title.ifBlank { "<title missing>" }
        return "${lineSeparator()}title ${safeTitle}${lineSeparator()}"
    }

    companion object {
        @JvmStatic
        fun titled(title: String): PageTitle = PageTitle(title)
    }
}