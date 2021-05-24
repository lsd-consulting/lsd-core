package com.lsd.domain.scenario.events;

import lombok.Builder;
import lombok.Value;

import static java.lang.String.format;

@Value
@Builder
public class Message implements Interaction {
    String id;
    String label;
    String from;
    String to;
    Object data;

    public String toMarkup() {
        return format("%s -> %s:[[#%s %s]]",
                getFrom(), getTo(), getId(), getLabel());
    }
}
