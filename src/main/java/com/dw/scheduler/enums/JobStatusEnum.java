package com.dw.scheduler.enums;

import lombok.Getter;

/**
 * Job Status Enum
 *
 * @author DW Team
 * @version 1.0.0
 */
@Getter
public enum JobStatusEnum {

    /**
     * Paused
     */
    PAUSED(0, "Paused"),

    /**
     * Running
     */
    RUNNING(1, "Running");

    private final Integer code;
    private final String desc;

    JobStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static JobStatusEnum getByCode(Integer code) {
        for (JobStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
