package com.lsd.domain.scenario.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.lang.String.format;

@Value
@RequiredArgsConstructor
public class NoteLeft implements Event {
    String value;

    public String toMarkup() {
        return format("note left: %s", getValue());
    }
}
