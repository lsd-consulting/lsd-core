package com.lsd;

import com.lsd.report.model.Participant;
import com.lsd.diagram.ValidComponentName;

public enum ParticipantType {
    ACTOR("actor"),
    BOUNDARY("boundary"),
    COLLECTIONS("collections"),
    CONTROL("control"),
    DATABASE("database"),
    ENTITY("entity"),
    PARTICIPANT("participant"),
    QUEUE("queue");

    private final String type;

    ParticipantType(final String type) {
        this.type = type;
    }

    public Participant called(String name) {
        return new Participant(String.format("%s %s", type, ValidComponentName.of(name)));
    }

    public Participant called(String name, String alias) {
        return new Participant(String.format("%s %s as \"%s\"", type, ValidComponentName.of(name), alias));
    }
}
