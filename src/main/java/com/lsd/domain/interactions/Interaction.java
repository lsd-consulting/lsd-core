package com.lsd.domain.interactions;

public interface Interaction {
    String getId();
    String getLabel();
    String toMarkup();
    Object getData();
}
