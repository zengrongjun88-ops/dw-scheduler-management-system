package com.dw.scheduler.enums;

public enum DependencyType {
    STRONG("强依赖"),
    WEAK("弱依赖"),
    CONDITIONAL("条件依赖");

    private final String description;

    DependencyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
