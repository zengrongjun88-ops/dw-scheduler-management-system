package com.dw.scheduler.enums;

public enum ServerStatus {
    ONLINE("在线"),
    OFFLINE("离线"),
    FAULT("故障");

    private final String description;

    ServerStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
