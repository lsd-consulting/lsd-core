package com.lsd.domain.scenario.events;

public interface Interaction extends Event {
    String getId();

    String getLabel();
    
    @SuppressWarnings("unused")
    Object getData();
}
