package com.lsd.report.model;

import com.lsd.events.SequenceEvent;
import com.lsd.properties.LsdProperties;

import static com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH;
import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * This type of event holds extra data in addition to what is displayed on the diagram and therefore
 * we can use popups etc. to display this data.
 */
public interface DataHolder extends SequenceEvent {
    String getId();

    String getLabel();

    @SuppressWarnings("unused")
    Object getData();

    /**
     * @return An abbreviation of the label if the length exceeds the configured maximum width.
     */
    default String abbreviatedLabel() {
        return abbreviate(getLabel().strip(), "...", LsdProperties.getInt(LABEL_MAX_WIDTH));
    }
}
