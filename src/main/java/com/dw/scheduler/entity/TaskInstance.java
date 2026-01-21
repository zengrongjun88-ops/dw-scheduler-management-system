package com.dw.scheduler.entity;

import com.dw.scheduler.enums.InstanceStatus;
import com.dw.scheduler.enums.TriggerType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实例实体
 */
@Data
@Entity
@Table(name = "t_task_instance", indexes = {
        @Index(name = "idx_task_id", columnList = "task_id"),
        @Index(name = "idx_business_date", columnList = "business_date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_start_time", columnList = "start_time"),
        @Index(name = "idx_worker_id", columnList = "worker_id"),
        @Index(name = "idx_create_time", columnList = "create_time"),
        @Index(name = "idx_task_business_date", columnList = "task_id,business_date"),
        @Index(name = "idx_status_create_time", columnList = "status,create_time")
})
@Where(clause = "deleted = 0")
public class TaskInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instance_name", length = 256)
    private String instanceName;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "task_snapshot", columnDefinition = "TEXT")
    private String taskSnapshot;

    @Column(name = "business_date")
    private LocalDate businessDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private InstanceStatus status = InstanceStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", length = 16)
    private TriggerType triggerType;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "execute_time")
    private Integer executeTime;

    @Column(name = "worker_id", length = 64)
    private String workerId;

    @Column(name = "retry_times")
    private Integer retryTimes = 0;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    @Column(name = "deleted")
    private Integer deleted = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
