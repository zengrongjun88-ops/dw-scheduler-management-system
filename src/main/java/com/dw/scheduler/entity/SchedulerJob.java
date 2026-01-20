package com.dw.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Scheduler Job Entity
 *
 * @author DW Team
 * @version 1.0.0
 */
@Data
@TableName("scheduler_job")
@ApiModel(value = "SchedulerJob", description = "Scheduler Job Entity")
public class SchedulerJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Job ID")
    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @ApiModelProperty(value = "Job Name", required = true)
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty(value = "Job Group", required = true)
    @TableField("job_group")
    private String jobGroup;

    @ApiModelProperty(value = "Job Execution Class", required = true)
    @TableField("job_class")
    private String jobClass;

    @ApiModelProperty(value = "Cron Expression", required = true)
    @TableField("cron_expression")
    private String cronExpression;

    @ApiModelProperty(value = "Status: 0-Paused, 1-Running", required = true)
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "Job Description")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "Job Data (JSON format)")
    @TableField("job_data")
    private String jobData;

    @ApiModelProperty(value = "Create Time")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "Update Time")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "Creator")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "Updater")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "Delete Flag: 0-Not Deleted, 1-Deleted")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
