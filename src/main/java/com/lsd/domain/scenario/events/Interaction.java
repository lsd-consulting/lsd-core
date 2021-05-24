package com.lsd.domain.scenario.events;

import java.util.Optional;

public interface Interaction extends Event {
    String getId();

    String getLabel();

    @SuppressWarnings("unused")
    Object getData();

    /**
     * The label minus any plantUml placeholders (for images/icons etc.) that render nicely in plantUml but not in html.
     */
    @SuppressWarnings("unused")
    default String sanitisedLabel() {
        return Optional.ofNullable(getLabel())
                .map(label -> label.replaceAll("<\\$.*?>", ""))
                .orElse("");
    }
}
