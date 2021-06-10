package com.lsd;

// Order matters for sorting by importance
public enum OutcomeStatus {
    ERROR,
    WARN,
    SUCCESS;

    public String getCssClass() {
        return name().toLowerCase();
    }
}
