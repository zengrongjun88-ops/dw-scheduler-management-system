package com.dw.scheduler.enums;

public enum TaskType {
    SQL("SQL任务"),
    SHELL("Shell脚本任务"),
    PYTHON("Python任务"),
    SPARK("Spark任务");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
