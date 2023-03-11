package com.lsd.events

import java.lang.System.lineSeparator

/**
 * Used to indicate that a new page should be created in a sequence diagram.
 */
class Newpage(val pageTitle: PageTitle) : SequenceEvent {
    private val keyword = "newpage"

    override fun toMarkup(): String {
        val titleMarkup = pageTitle.title.let { if (it.isEmpty()) "" else " '$it'" }
        return "${lineSeparator()}$keyword$titleMarkup${lineSeparator()}"
    }

    companion object {
        @JvmStatic
        fun titled(title: String): Newpage = Newpage(PageTitle(title))
    }
}