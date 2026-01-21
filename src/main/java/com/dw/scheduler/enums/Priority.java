package com.dw.scheduler.enums;

public enum Priority {
    HIGH("高"),
    MEDIUM("中"),
    LOW("低");

    private final String description;

    Priority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
