package com.lsd.report.model

import j2html.TagCreator
import j2html.tags.ContainerTag
import j2html.tags.UnescapedText

/**
 * Simplifies adding content that is only displayed when a hyperlink is clicked - the id used on the href must match.
 */
object PopupContent {
    @JvmStatic
    fun popupDiv(id: String, title: String, content: String): ContainerTag<*> {
        return TagCreator.div().withId(id).withClass("overlay").attr("onclick", "location.href='#!';").with(
            TagCreator.div().withClass("popup").attr("onclick", "event.stopPropagation();").with(
                TagCreator.h2(title),
                TagCreator.a().withClass("close").withHref("#!").with(UnescapedText("&times;")),
                TagCreator.div().withClass("content").with(UnescapedText(content))
            )
        )
    }
}