package com.lsd.domain.scenario.events;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Markup implements Event {
    String value;

    public String toMarkup() {
        return value;
    }
}
