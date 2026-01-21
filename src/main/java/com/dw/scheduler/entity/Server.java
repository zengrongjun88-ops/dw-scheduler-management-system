package com.dw.scheduler.entity;

import com.dw.scheduler.enums.ServerRole;
import com.dw.scheduler.enums.ServerStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务器实体
 */
@Data
@Entity
@Table(name = "t_server", indexes = {
        @Index(name = "uk_ip_address", columnList = "ip_address", unique = true),
        @Index(name = "idx_server_role", columnList = "server_role"),
        @Index(name = "idx_resource_group", columnList = "resource_group"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_last_heartbeat", columnList = "last_heartbeat")
})
@Where(clause = "deleted = 0")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_name", length = 128)
    private String serverName;

    @Column(name = "ip_address", nullable = false, unique = true, length = 64)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "server_role", nullable = false, length = 16)
    private ServerRole serverRole;

    @Column(name = "resource_group", length = 64)
    private String resourceGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16)
    private ServerStatus status = ServerStatus.OFFLINE;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column(name = "memory_size")
    private Long memorySize;

    @Column(name = "disk_size")
    private Long diskSize;

    @Column(name = "max_task_num")
    private Integer maxTaskNum = 100;

    @Column(name = "current_task_num")
    private Integer currentTaskNum = 0;

    @Column(name = "cpu_usage", precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Column(name = "memory_usage", precision = 5, scale = 2)
    private BigDecimal memoryUsage;

    @Column(name = "disk_usage", precision = 5, scale = 2)
    private BigDecimal diskUsage;

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(name = "deleted")
    private Integer deleted = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
