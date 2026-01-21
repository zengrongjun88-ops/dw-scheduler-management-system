package com.dw.scheduler.enums;

public enum ServerRole {
    MASTER("Master节点"),
    WORKER("Worker节点");

    private final String description;

    ServerRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
