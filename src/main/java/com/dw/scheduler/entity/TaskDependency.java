package com.dw.scheduler.entity;

import com.dw.scheduler.enums.DependencyType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 任务依赖关系实体
 */
@Data
@Entity
@Table(name = "t_task_dependency", indexes = {
        @Index(name = "idx_task_id", columnList = "task_id"),
        @Index(name = "idx_depend_task_id", columnList = "depend_task_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_task_depend", columnNames = {"task_id", "depend_task_id", "deleted"})
})
@Where(clause = "deleted = 0")
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "depend_task_id", nullable = false)
    private Long dependTaskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "depend_type", length = 16)
    private DependencyType dependType = DependencyType.STRONG;

    @Column(name = "cycle_offset")
    private Integer cycleOffset = 0;

    @Column(name = "deleted")
    private Integer deleted = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @Column(name = "create_by", length = 64)
    private String createBy;
}
