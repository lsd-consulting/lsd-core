package com.lsd.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * For providing raw plantUml markup when none of the provided event types are sufficient.
 */
@Value
@RequiredArgsConstructor
public class Markup implements SequenceEvent {
    String value;

    public String toMarkup() {
        return value;
    }
}
