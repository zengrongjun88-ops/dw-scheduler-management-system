package com.dw.scheduler.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 任务目录实体
 */
@Data
@Entity
@Table(name = "t_directory")
@Where(clause = "deleted = 0")
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "directory_name", nullable = false, length = 128)
    private String directoryName;

    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L;

    @Column(name = "directory_path", length = 512)
    private String directoryPath;

    @Column(name = "owner", length = 64)
    private String owner;

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
