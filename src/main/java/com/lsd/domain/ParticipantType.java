package com.lsd.domain;

import org.apache.commons.text.WordUtils;

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
        return new Participant(String.format("%s %s", type, removeSpaces(name)));
    }

    public Participant called(String name, String alias) {
        return new Participant(String.format("%s %s as \"%s\"", type, removeSpaces(name), alias));
    }

    private String removeSpaces(String s) {
        return WordUtils.capitalizeFully(s)
                .replaceAll(" ", "");
    }
}
