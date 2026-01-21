package com.dw.scheduler.controller;

import com.dw.scheduler.common.Result;
import com.dw.scheduler.entity.Task;
import com.dw.scheduler.entity.TaskDependency;
import com.dw.scheduler.enums.TaskStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 任务管理控制器
 * 提供任务的完整生命周期管理，包括CRUD、状态变更、依赖管理、DAG查询等
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/tasks")
@Api(tags = "任务管理", description = "任务的创建、更新、删除、查询以及依赖关系管理")
public class TaskController {

    /**
     * 查询任务列表（支持分页和多条件搜索）
     * 支持的查询条件：
     * - taskName: 任务名称（模糊匹配）
     * - taskType: 任务类型
     * - status: 任务状态
     * - directoryId: 所属目录ID
     * - owner: 任务负责人
     * - subject: 主题/业务域
     *
     * @param taskName 任务名称（可选）
     * @param taskType 任务类型（可选）
     * @param status 任务状态（可选）
     * @param directoryId 目录ID（可选）
     * @param owner 负责人（可选）
     * @param subject 主题（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页任务列表
     */
    @GetMapping
    @ApiOperation(value = "查询任务列表", notes = "支持分页和多条件组合查询任务列表")
    public Result<Page<Task>> queryTasks(
            @ApiParam(value = "任务名称（模糊查询）", example = "etl_task")
            @RequestParam(required = false) String taskName,
            @ApiParam(value = "任务类型", example = "SQL")
            @RequestParam(required = false) String taskType,
            @ApiParam(value = "任务状态", example = "ONLINE")
            @RequestParam(required = false) TaskStatus status,
            @ApiParam(value = "目录ID", example = "1")
            @RequestParam(required = false) Long directoryId,
            @ApiParam(value = "负责人", example = "admin")
            @RequestParam(required = false) String owner,
            @ApiParam(value = "主题", example = "数据仓库")
            @RequestParam(required = false) String subject,
            @ApiParam(value = "页码（从0开始）", example = "0")
            @RequestParam(defaultValue = "0") Integer page,
            @ApiParam(value = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer size) {
        log.info("查询任务列表, taskName: {}, taskType: {}, status: {}, directoryId: {}, owner: {}, subject: {}, page: {}, size: {}",
                taskName, taskType, status, directoryId, owner, subject, page, size);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 根据ID查询任务详情
     * 返回任务的完整信息，包括代码、调度配置、告警配置等
     *
     * @param id 任务ID
     * @return 任务详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询任务详情", notes = "根据任务ID查询任务的完整配置信息")
    public Result<Task> getTaskById(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id) {
        log.info("查询任务详情, id: {}", id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 创建新任务
     * 创建时任务状态默认为 DEVELOPING（开发中）
     *
     * @param task 任务信息
     * @return 创建后的任务信息（包含自动生成的ID）
     */
    @PostMapping
    @ApiOperation(value = "创建任务", notes = "创建新的调度任务，默认状态为开发中")
    public Result<Task> createTask(
            @ApiParam(value = "任务信息", required = true)
            @Valid @RequestBody Task task) {
        log.info("创建任务, taskName: {}, taskType: {}, directoryId: {}",
                task.getTaskName(), task.getTaskType(), task.getDirectoryId());
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 更新任务信息
     * 可更新任务的所有配置信息，包括代码、调度参数、超时重试等
     * 注意：任务状态的修改请使用专门的状态变更接口
     *
     * @param id 任务ID
     * @param task 更新的任务信息
     * @return 更新后的任务信息
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新任务", notes = "更新任务的配置信息，不包括状态变更")
    public Result<Task> updateTask(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id,
            @ApiParam(value = "任务信息", required = true)
            @Valid @RequestBody Task task) {
        log.info("更新任务, id: {}, taskName: {}", id, task.getTaskName());
        task.setId(id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 删除任务
     * 逻辑删除，不物理删除数据
     * 注意：删除前需要检查是否存在依赖关系和运行中的实例
     *
     * @param id 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除任务", notes = "逻辑删除任务，删除前会检查依赖关系和运行实例")
    public Result<Void> deleteTask(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id) {
        log.info("删除任务, id: {}", id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 修改任务状态
     * 支持的状态流转：
     * - DEVELOPING -> TESTING: 开发完成，提交测试
     * - TESTING -> ONLINE: 测试通过，上线
     * - ONLINE -> OFFLINE: 下线任务
     * - OFFLINE -> ONLINE: 重新上线
     *
     * @param id 任务ID
     * @param status 目标状态
     * @return 更新后的任务信息
     */
    @PutMapping("/{id}/status")
    @ApiOperation(value = "修改任务状态", notes = "变更任务的运行状态（开发中/测试中/已上线/已下线）")
    public Result<Task> updateTaskStatus(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id,
            @ApiParam(value = "目标状态", required = true, example = "ONLINE")
            @RequestParam @NotNull(message = "状态不能为空") TaskStatus status) {
        log.info("修改任务状态, id: {}, status: {}", id, status);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 查询任务的依赖关系
     * 返回当前任务依赖的所有上游任务
     *
     * @param id 任务ID
     * @return 依赖关系列表
     */
    @GetMapping("/{id}/dependencies")
    @ApiOperation(value = "查询任务依赖", notes = "查询任务的所有上游依赖关系")
    public Result<List<TaskDependency>> getTaskDependencies(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id) {
        log.info("查询任务依赖关系, id: {}", id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 添加任务依赖
     * 建立任务之间的依赖关系，支持多种依赖类型：
     * - SELF_DEPEND: 自依赖（依赖自己的上一周期）
     * - NORMAL_DEPEND: 普通依赖（依赖其他任务的当前周期）
     * - CROSS_DEPEND: 跨周期依赖（依赖其他任务的特定周期）
     *
     * @param id 任务ID
     * @param dependency 依赖关系信息
     * @return 创建后的依赖关系
     */
    @PostMapping("/{id}/dependencies")
    @ApiOperation(value = "添加任务依赖", notes = "为任务添加上游依赖关系，支持多种依赖类型")
    public Result<TaskDependency> addTaskDependency(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id,
            @ApiParam(value = "依赖关系信息", required = true)
            @Valid @RequestBody TaskDependency dependency) {
        log.info("添加任务依赖, taskId: {}, dependTaskId: {}, dependencyType: {}",
                id, dependency.getDependTaskId(), dependency.getDependencyType());
        dependency.setTaskId(id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 删除任务依赖
     * 移除指定的依赖关系
     *
     * @param id 任务ID
     * @param depId 依赖关系ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}/dependencies/{depId}")
    @ApiOperation(value = "删除任务依赖", notes = "删除指定的任务依赖关系")
    public Result<Void> deleteTaskDependency(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id,
            @ApiParam(value = "依赖关系ID", required = true, example = "10")
            @PathVariable("depId") @NotNull(message = "依赖关系ID不能为空") Long depId) {
        log.info("删除任务依赖, taskId: {}, dependencyId: {}", id, depId);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 查询任务DAG图
     * 返回任务的完整依赖关系图，包括所有上游和下游任务
     * 用于前端可视化展示任务的依赖拓扑
     *
     * @param id 任务ID
     * @return DAG图数据（节点和边的集合）
     */
    @GetMapping("/{id}/dag")
    @ApiOperation(value = "查询任务DAG图", notes = "获取任务的依赖关系拓扑图，用于可视化展示")
    public Result<Map<String, Object>> getTaskDag(
            @ApiParam(value = "任务ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "任务ID不能为空") Long id) {
        log.info("查询任务DAG图, id: {}", id);
        // TODO: 调用Service层实现
        // 返回格式示例：
        // {
        //   "nodes": [{"id": 1, "name": "task1", "type": "SQL"}, ...],
        //   "edges": [{"source": 1, "target": 2, "type": "NORMAL_DEPEND"}, ...]
        // }
        return Result.success();
    }
}
