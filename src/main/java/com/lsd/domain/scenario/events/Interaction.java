package com.lsd.domain.scenario.events;

import com.lsd.properties.LsdProperties;

import static com.lsd.properties.DefaultProperties.LABEL_MAX_WIDTH;
import static org.apache.commons.lang3.StringUtils.abbreviate;

public interface Interaction extends Event {
    String getId();

    String getLabel();

    @SuppressWarnings("unused")
    Object getData();

    /**
     * @return An abbreviation of the label if the length exceeds the configured maximum width.
     */
    default String abbreviatedLabel() {
        return abbreviate(getLabel(), "...", LsdProperties.getInt(LABEL_MAX_WIDTH));
    }
}
