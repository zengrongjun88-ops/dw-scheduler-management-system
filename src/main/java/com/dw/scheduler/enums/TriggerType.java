package com.dw.scheduler.enums;

public enum TriggerType {
    SCHEDULE("自动调度"),
    MANUAL("手动触发"),
    API("API触发"),
    RERUN("重跑");

    private final String description;

    TriggerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
