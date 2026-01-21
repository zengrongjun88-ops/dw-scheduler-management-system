package com.dw.scheduler.repository;

import com.dw.scheduler.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    /**
     * 查询任务的所有上游依赖
     */
    List<TaskDependency> findByTaskId(Long taskId);

    /**
     * 查询任务的所有下游依赖
     */
    List<TaskDependency> findByDependTaskId(Long dependTaskId);

    /**
     * 查询两个任务之间是否存在依赖关系
     */
    TaskDependency findByTaskIdAndDependTaskId(Long taskId, Long dependTaskId);

    /**
     * 删除任务的所有依赖关系
     */
    @Modifying
    @Query("update TaskDependency t set t.deleted = 1 where t.taskId = ?1")
    void deleteByTaskId(Long taskId);
}
