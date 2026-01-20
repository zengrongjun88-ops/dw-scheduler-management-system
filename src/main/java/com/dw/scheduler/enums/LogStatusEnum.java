package com.dw.scheduler.enums;

import lombok.Getter;

/**
 * Job Log Status Enum
 *
 * @author DW Team
 * @version 1.0.0
 */
@Getter
public enum LogStatusEnum {

    /**
     * Failed
     */
    FAILED(0, "Failed"),

    /**
     * Success
     */
    SUCCESS(1, "Success");

    private final Integer code;
    private final String desc;

    LogStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static LogStatusEnum getByCode(Integer code) {
        for (LogStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
