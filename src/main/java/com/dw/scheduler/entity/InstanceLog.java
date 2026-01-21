package com.dw.scheduler.entity;

import com.dw.scheduler.enums.LogLevel;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 实例日志实体
 */
@Data
@Entity
@Table(name = "t_instance_log", indexes = {
        @Index(name = "idx_instance_id", columnList = "instance_id"),
        @Index(name = "idx_instance_log_time", columnList = "instance_id,log_time")
})
public class InstanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    @Column(name = "log_content", columnDefinition = "TEXT")
    private String logContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", length = 16)
    private LogLevel logLevel = LogLevel.INFO;

    @Column(name = "log_time")
    private LocalDateTime logTime;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}
