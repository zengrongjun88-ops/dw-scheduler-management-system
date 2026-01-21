package com.dw.scheduler.entity;

import com.dw.scheduler.enums.Priority;
import com.dw.scheduler.enums.TaskStatus;
import com.dw.scheduler.enums.TaskType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 任务实体
 */
@Data
@Entity
@Table(name = "t_task", indexes = {
        @Index(name = "idx_task_name", columnList = "task_name", unique = true),
        @Index(name = "idx_task_type", columnList = "task_type"),
        @Index(name = "idx_directory_id", columnList = "directory_id"),
        @Index(name = "idx_owner", columnList = "owner"),
        @Index(name = "idx_subject", columnList = "subject"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_create_time", columnList = "create_time"),
        @Index(name = "idx_update_time", columnList = "update_time")
})
@Where(clause = "deleted = 0")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false, unique = true, length = 128)
    private String taskName;

    @Column(name = "task_code", columnDefinition = "TEXT")
    private String taskCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 32)
    private TaskType taskType;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "directory_id")
    private Long directoryId;

    @Column(name = "cron_expr", length = 128)
    private String cronExpr;

    @Column(name = "offset_days")
    private Integer offsetDays = 0;

    @Column(name = "timeout")
    private Integer timeout = 60;

    @Column(name = "retry_times")
    private Integer retryTimes = 0;

    @Column(name = "retry_interval")
    private Integer retryInterval = 5;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 16)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "owner", nullable = false, length = 256)
    private String owner;

    @Column(name = "subject", length = 64)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private TaskStatus status = TaskStatus.DEVELOPING;

    @Column(name = "resource_group", length = 64)
    private String resourceGroup = "DEFAULT";

    @Column(name = "max_concurrent")
    private Integer maxConcurrent = 1;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "alert_enabled")
    private Integer alertEnabled = 1;

    @Column(name = "alert_users", length = 500)
    private String alertUsers;

    @Column(name = "deleted")
    private Integer deleted = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "create_by", length = 64)
    private String createBy;

    @Column(name = "update_by", length = 64)
    private String updateBy;
}
