package com.lsd.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Markup implements SequenceEvent {
    String value;

    public String toMarkup() {
        return value;
    }
}
