package com.dw.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Scheduler Job Log Entity
 *
 * @author DW Team
 * @version 1.0.0
 */
@Data
@TableName("scheduler_job_log")
@ApiModel(value = "SchedulerJobLog", description = "Scheduler Job Log Entity")
public class SchedulerJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Log ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty(value = "Job ID", required = true)
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty(value = "Job Name", required = true)
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty(value = "Job Group", required = true)
    @TableField("job_group")
    private String jobGroup;

    @ApiModelProperty(value = "Execution Status: 0-Failed, 1-Success", required = true)
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "Start Time", required = true)
    @TableField("start_time")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "End Time")
    @TableField("end_time")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "Execution Duration (milliseconds)")
    @TableField("execute_time")
    private Long executeTime;

    @ApiModelProperty(value = "Execution Message")
    @TableField("message")
    private String message;

    @ApiModelProperty(value = "Exception Information")
    @TableField("exception_info")
    private String exceptionInfo;

    @ApiModelProperty(value = "Create Time")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
