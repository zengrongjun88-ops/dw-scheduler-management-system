package com.dw.scheduler.service;

import com.dw.scheduler.entity.Task;
import com.dw.scheduler.entity.TaskDependency;
import com.dw.scheduler.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 *
 * @author DW Team
 */
public interface TaskService {

    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 创建的任务
     */
    Task createTask(Task task);

    /**
     * 更新任务
     *
     * @param id   任务ID
     * @param task 任务信息
     * @return 更新后的任务
     */
    Task updateTask(Long id, Task task);

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    void deleteTask(Long id);

    /**
     * 查询任务列表(分页和多条件搜索)
     *
     * @param taskName      任务名称(模糊查询)
     * @param taskType      任务类型
     * @param status        任务状态
     * @param owner         责任人(模糊查询)
     * @param subject       主题
     * @param directoryId   目录ID
     * @param resourceGroup 资源分组
     * @param pageable      分页参数
     * @return 任务分页列表
     */
    Page<Task> getTaskList(String taskName, String taskType, TaskStatus status,
                           String owner, String subject, Long directoryId,
                           String resourceGroup, Pageable pageable);

    /**
     * 查询任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    Task getTaskById(Long id);

    /**
     * 修改任务状态
     *
     * @param id     任务ID
     * @param status 新状态
     * @return 更新后的任务
     */
    Task updateTaskStatus(Long id, TaskStatus status);

    /**
     * 添加任务依赖
     *
     * @param dependency 依赖关系
     * @return 添加的依赖关系
     */
    TaskDependency addTaskDependency(TaskDependency dependency);

    /**
     * 删除任务依赖
     *
     * @param taskId       任务ID
     * @param dependTaskId 依赖任务ID
     */
    void deleteTaskDependency(Long taskId, Long dependTaskId);

    /**
     * 查询任务的上游依赖
     *
     * @param taskId 任务ID
     * @return 上游依赖列表
     */
    List<TaskDependency> getUpstreamDependencies(Long taskId);

    /**
     * 查询任务的下游依赖
     *
     * @param taskId 任务ID
     * @return 下游依赖列表
     */
    List<TaskDependency> getDownstreamDependencies(Long taskId);

    /**
     * 循环依赖检测
     *
     * @param taskId       任务ID
     * @param dependTaskId 待添加的依赖任务ID
     * @return 是否存在循环依赖
     */
    boolean hasCyclicDependency(Long taskId, Long dependTaskId);

    /**
     * 查询任务DAG图数据
     *
     * @param taskId 任务ID
     * @param depth  递归深度(0表示不限制)
     * @return DAG图数据,包含节点和边
     */
    Map<String, Object> getTaskDag(Long taskId, int depth);
}
