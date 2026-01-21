package com.dw.scheduler.repository;

import com.dw.scheduler.entity.TaskInstance;
import com.dw.scheduler.enums.InstanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long>, JpaSpecificationExecutor<TaskInstance> {

    /**
     * 根据任务ID查询实例列表
     */
    List<TaskInstance> findByTaskIdOrderByCreateTimeDesc(Long taskId);

    /**
     * 根据任务ID和业务日期查询实例
     */
    List<TaskInstance> findByTaskIdAndBusinessDate(Long taskId, LocalDate businessDate);

    /**
     * 根据状态查询实例列表
     */
    List<TaskInstance> findByStatus(InstanceStatus status);

    /**
     * 根据Worker ID查询运行中的实例
     */
    List<TaskInstance> findByWorkerIdAndStatus(String workerId, InstanceStatus status);

    /**
     * 查询最近N条实例记录
     */
    List<TaskInstance> findTop10ByTaskIdOrderByCreateTimeDesc(Long taskId);
}
