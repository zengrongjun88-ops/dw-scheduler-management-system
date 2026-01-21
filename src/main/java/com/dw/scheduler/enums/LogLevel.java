package com.dw.scheduler.enums;

public enum LogLevel {
    INFO("信息"),
    WARN("警告"),
    ERROR("错误");

    private final String description;

    LogLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
