package com.dw.scheduler.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据源实体
 */
@Data
@Entity
@Table(name = "t_datasource", indexes = {
        @Index(name = "uk_datasource_name", columnList = "datasource_name", unique = true)
})
@Where(clause = "deleted = 0")
public class Datasource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datasource_name", nullable = false, unique = true, length = 128)
    private String datasourceName;

    @Column(name = "datasource_type", nullable = false, length = 32)
    private String datasourceType;

    @Column(name = "jdbc_url", nullable = false, length = 512)
    private String jdbcUrl;

    @Column(name = "username", length = 128)
    private String username;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "driver_class", nullable = false, length = 256)
    private String driverClass;

    @Column(name = "description", length = 500)
    private String description;

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
