package com.dw.scheduler.enums;

public enum TaskStatus {
    DEVELOPING("开发中"),
    ENABLED("已启用"),
    DISABLED("已禁用");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
