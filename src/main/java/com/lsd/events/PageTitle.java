package com.lsd.events;

import lombok.Value;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Used to indicate that a new page should be created in a sequence diagram.
 */
@Value(staticConstructor = "titled")
public class PageTitle implements SequenceEvent {
    String keyword = "title";
    String title;

    public String toMarkup() {
        String titleValue = isBlank(title) ? "<title missing>" : " " + title;
        return lineSeparator() + keyword + titleValue + lineSeparator();
    }
}
