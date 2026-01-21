package com.dw.scheduler.repository;

import com.dw.scheduler.entity.Task;
import com.dw.scheduler.enums.TaskStatus;
import com.dw.scheduler.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * 根据任务名称查询
     */
    Task findByTaskName(String taskName);

    /**
     * 根据目录ID查询任务列表
     */
    List<Task> findByDirectoryId(Long directoryId);

    /**
     * 根据任务状态查询
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * 根据任务类型查询
     */
    List<Task> findByTaskType(TaskType taskType);

    /**
     * 根据责任人查询
     */
    List<Task> findByOwnerContaining(String owner);

    /**
     * 根据资源分组查询
     */
    List<Task> findByResourceGroup(String resourceGroup);
}
