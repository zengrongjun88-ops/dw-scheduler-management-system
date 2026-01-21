package com.dw.scheduler.enums;

public enum InstanceStatus {
    WAITING("等待中"),
    RUNNING("运行中"),
    SUCCESS("成功"),
    FAILED("失败"),
    CANCELED("已取消");

    private final String description;

    InstanceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
