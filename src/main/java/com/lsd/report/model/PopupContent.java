package com.lsd.report.model;

import j2html.tags.ContainerTag;
import j2html.tags.UnescapedText;

import static j2html.TagCreator.*;

/**
 * Simplifies adding content that is only displayed when a hyperlink is clicked - the id used on the href must match.
 */
public class PopupContent {

    public static ContainerTag popupDiv(String id, String title, String content) {
        return div().withId(id).withClass("overlay").attr("onclick", "location.href='#!';").with(
                div().withClass("popup").attr("onclick", "event.stopPropagation();").with(
                        h2(title),
                        a().withClass("close").withHref("#!").with(new UnescapedText("&times;")),
                        div().withClass("content").with(
                                pre(content)
                        )
                )
        );
    }
}
