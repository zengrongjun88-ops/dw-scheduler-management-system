package com.dw.scheduler.common;

import lombok.Getter;

/**
 * Result Code Enum
 *
 * @author DW Team
 * @version 1.0.0
 */
@Getter
public enum ResultCode {

    /**
     * Success
     */
    SUCCESS(200, "Success"),

    /**
     * Server Error
     */
    ERROR(500, "Server Error"),

    /**
     * Bad Request
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * Unauthorized
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * Forbidden
     */
    FORBIDDEN(403, "Forbidden"),

    /**
     * Not Found
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * Validation Error
     */
    VALIDATION_ERROR(422, "Validation Error"),

    /**
     * Job Not Found
     */
    JOB_NOT_FOUND(1001, "Job Not Found"),

    /**
     * Job Already Exists
     */
    JOB_ALREADY_EXISTS(1002, "Job Already Exists"),

    /**
     * Invalid Cron Expression
     */
    INVALID_CRON_EXPRESSION(1003, "Invalid Cron Expression"),

    /**
     * Job Start Failed
     */
    JOB_START_FAILED(1004, "Job Start Failed"),

    /**
     * Job Pause Failed
     */
    JOB_PAUSE_FAILED(1005, "Job Pause Failed"),

    /**
     * Job Resume Failed
     */
    JOB_RESUME_FAILED(1006, "Job Resume Failed"),

    /**
     * Job Delete Failed
     */
    JOB_DELETE_FAILED(1007, "Job Delete Failed"),

    /**
     * Job Execute Failed
     */
    JOB_EXECUTE_FAILED(1008, "Job Execute Failed");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
