package com.lsd.parse;

import com.lsd.events.SequenceEvent;

import java.util.Optional;

public interface Parser {
    Optional<SequenceEvent> parse(String message, String body);
}
