package com.dw.scheduler.service;

import com.dw.scheduler.entity.InstanceLog;
import com.dw.scheduler.entity.TaskInstance;
import com.dw.scheduler.enums.InstanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 任务实例服务接口
 *
 * @author DW Team
 */
public interface TaskInstanceService {

    /**
     * 查询实例列表(分页和多条件搜索)
     *
     * @param instanceName  实例名称(模糊查询)
     * @param taskId        任务ID
     * @param status        实例状态
     * @param businessDate  业务日期
     * @param startDateFrom 开始时间范围-起
     * @param startDateTo   开始时间范围-止
     * @param pageable      分页参数
     * @return 实例分页列表
     */
    Page<TaskInstance> getInstanceList(String instanceName, Long taskId, InstanceStatus status,
                                       LocalDate businessDate, LocalDate startDateFrom,
                                       LocalDate startDateTo, Pageable pageable);

    /**
     * 查询实例详情
     *
     * @param id 实例ID
     * @return 实例详情
     */
    TaskInstance getInstanceById(Long id);

    /**
     * 查询实例日志
     *
     * @param instanceId 实例ID
     * @return 日志列表
     */
    List<InstanceLog> getInstanceLogs(Long instanceId);

    /**
     * 查询实例DAG图数据
     *
     * @param instanceId 实例ID
     * @return DAG图数据,包含节点和边
     */
    Map<String, Object> getInstanceDag(Long instanceId);

    /**
     * 创建实例(预留接口)
     *
     * @param instance 实例信息
     * @return 创建的实例
     */
    TaskInstance createInstance(TaskInstance instance);
}
