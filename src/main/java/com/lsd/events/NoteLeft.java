package com.lsd.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.lang.String.format;

@Value
@RequiredArgsConstructor
public class NoteLeft implements SequenceEvent {
    String value;

    public String toMarkup() {
        return format("note left: %s", getValue());
    }
}
