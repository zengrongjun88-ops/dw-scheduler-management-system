package com.dw.scheduler.service.impl;

import com.dw.scheduler.entity.Task;
import com.dw.scheduler.entity.TaskDependency;
import com.dw.scheduler.enums.TaskStatus;
import com.dw.scheduler.enums.TaskType;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.repository.TaskDependencyRepository;
import com.dw.scheduler.repository.TaskRepository;
import com.dw.scheduler.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * 任务服务实现类
 *
 * @author DW Team
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    /**
     * 创建任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task createTask(Task task) {
        log.info("创建任务: {}", task.getTaskName());

        // 参数校验
        validateTask(task);

        // 检查任务名称是否已存在
        Task existTask = taskRepository.findByTaskName(task.getTaskName());
        if (existTask != null) {
            throw new BusinessException("任务名称已存在");
        }

        // 设置默认值
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.DEVELOPING);
        }

        return taskRepository.save(task);
    }

    /**
     * 更新任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task updateTask(Long id, Task task) {
        log.info("更新任务: id={}, name={}", id, task.getTaskName());

        // 查询任务是否存在
        Task existTask = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 参数校验
        validateTask(task);

        // 检查任务名称是否重复(排除自己)
        Task sameNameTask = taskRepository.findByTaskName(task.getTaskName());
        if (sameNameTask != null && !sameNameTask.getId().equals(id)) {
            throw new BusinessException("任务名称已存在");
        }

        // 更新任务信息
        existTask.setTaskName(task.getTaskName());
        existTask.setTaskCode(task.getTaskCode());
        existTask.setTaskType(task.getTaskType());
        existTask.setDescription(task.getDescription());
        existTask.setDirectoryId(task.getDirectoryId());
        existTask.setCronExpr(task.getCronExpr());
        existTask.setOffsetDays(task.getOffsetDays());
        existTask.setTimeout(task.getTimeout());
        existTask.setRetryTimes(task.getRetryTimes());
        existTask.setRetryInterval(task.getRetryInterval());
        existTask.setPriority(task.getPriority());
        existTask.setOwner(task.getOwner());
        existTask.setSubject(task.getSubject());
        existTask.setResourceGroup(task.getResourceGroup());
        existTask.setMaxConcurrent(task.getMaxConcurrent());
        existTask.setDatasourceId(task.getDatasourceId());
        existTask.setAlertEnabled(task.getAlertEnabled());
        existTask.setAlertUsers(task.getAlertUsers());
        existTask.setUpdateBy(task.getUpdateBy());

        return taskRepository.save(existTask);
    }

    /**
     * 删除任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        log.info("删除任务: id={}", id);

        // 查询任务是否存在
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 检查任务状态,只能删除开发中的任务
        if (task.getStatus() != TaskStatus.DEVELOPING) {
            throw new BusinessException("只能删除开发中的任务");
        }

        // 检查是否存在依赖关系
        List<TaskDependency> upstreamDeps = taskDependencyRepository.findByTaskId(id);
        List<TaskDependency> downstreamDeps = taskDependencyRepository.findByDependTaskId(id);
        if (!upstreamDeps.isEmpty() || !downstreamDeps.isEmpty()) {
            throw new BusinessException("该任务存在依赖关系,请先删除依赖关系");
        }

        // 逻辑删除
        task.setDeleted(1);
        taskRepository.save(task);
    }

    /**
     * 查询任务列表(分页和多条件搜索)
     */
    @Override
    public Page<Task> getTaskList(String taskName, String taskType, TaskStatus status,
                                  String owner, String subject, Long directoryId,
                                  String resourceGroup, Pageable pageable) {
        log.info("查询任务列表: taskName={}, taskType={}, status={}", taskName, taskType, status);

        // 构建动态查询条件
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 任务名称模糊查询
            if (StringUtils.hasText(taskName)) {
                predicates.add(cb.like(root.get("taskName"), "%" + taskName + "%"));
            }

            // 任务类型
            if (StringUtils.hasText(taskType)) {
                try {
                    TaskType type = TaskType.valueOf(taskType);
                    predicates.add(cb.equal(root.get("taskType"), type));
                } catch (IllegalArgumentException e) {
                    log.warn("无效的任务类型: {}", taskType);
                }
            }

            // 任务状态
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 责任人模糊查询
            if (StringUtils.hasText(owner)) {
                predicates.add(cb.like(root.get("owner"), "%" + owner + "%"));
            }

            // 主题
            if (StringUtils.hasText(subject)) {
                predicates.add(cb.equal(root.get("subject"), subject));
            }

            // 目录ID
            if (directoryId != null) {
                predicates.add(cb.equal(root.get("directoryId"), directoryId));
            }

            // 资源分组
            if (StringUtils.hasText(resourceGroup)) {
                predicates.add(cb.equal(root.get("resourceGroup"), resourceGroup));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return taskRepository.findAll(spec, pageable);
    }

    /**
     * 查询任务详情
     */
    @Override
    public Task getTaskById(Long id) {
        log.info("查询任务详情: id={}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
    }

    /**
     * 修改任务状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task updateTaskStatus(Long id, TaskStatus status) {
        log.info("修改任务状态: id={}, status={}", id, status);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        if (status == null) {
            throw new BusinessException("任务状态不能为空");
        }

        task.setStatus(status);
        return taskRepository.save(task);
    }

    /**
     * 添加任务依赖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDependency addTaskDependency(TaskDependency dependency) {
        log.info("添加任务依赖: taskId={}, dependTaskId={}",
                dependency.getTaskId(), dependency.getDependTaskId());

        // 参数校验
        if (dependency.getTaskId() == null || dependency.getDependTaskId() == null) {
            throw new BusinessException("任务ID和依赖任务ID不能为空");
        }

        if (dependency.getTaskId().equals(dependency.getDependTaskId())) {
            throw new BusinessException("不能依赖自己");
        }

        // 检查任务是否存在
        Task task = taskRepository.findById(dependency.getTaskId())
                .orElseThrow(() -> new BusinessException("任务不存在"));
        Task dependTask = taskRepository.findById(dependency.getDependTaskId())
                .orElseThrow(() -> new BusinessException("依赖任务不存在"));

        // 检查依赖关系是否已存在
        TaskDependency existDependency = taskDependencyRepository.findByTaskIdAndDependTaskId(
                dependency.getTaskId(), dependency.getDependTaskId());
        if (existDependency != null) {
            throw new BusinessException("依赖关系已存在");
        }

        // 循环依赖检测
        if (hasCyclicDependency(dependency.getTaskId(), dependency.getDependTaskId())) {
            throw new BusinessException("添加该依赖会造成循环依赖");
        }

        return taskDependencyRepository.save(dependency);
    }

    /**
     * 删除任务依赖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskDependency(Long taskId, Long dependTaskId) {
        log.info("删除任务依赖: taskId={}, dependTaskId={}", taskId, dependTaskId);

        TaskDependency dependency = taskDependencyRepository.findByTaskIdAndDependTaskId(taskId, dependTaskId);
        if (dependency == null) {
            throw new BusinessException("依赖关系不存在");
        }

        // 逻辑删除
        dependency.setDeleted(1);
        taskDependencyRepository.save(dependency);
    }

    /**
     * 查询任务的上游依赖
     */
    @Override
    public List<TaskDependency> getUpstreamDependencies(Long taskId) {
        log.info("查询任务的上游依赖: taskId={}", taskId);

        return taskDependencyRepository.findByTaskId(taskId);
    }

    /**
     * 查询任务的下游依赖
     */
    @Override
    public List<TaskDependency> getDownstreamDependencies(Long taskId) {
        log.info("查询任务的下游依赖: taskId={}", taskId);

        return taskDependencyRepository.findByDependTaskId(taskId);
    }

    /**
     * 循环依赖检测
     * 使用拓扑排序算法(基于DFS)检测是否存在环
     */
    @Override
    public boolean hasCyclicDependency(Long taskId, Long dependTaskId) {
        log.info("循环依赖检测: taskId={}, dependTaskId={}", taskId, dependTaskId);

        // 构建依赖图
        Map<Long, List<Long>> graph = new HashMap<>();
        buildDependencyGraph(graph);

        // 临时添加新的依赖关系
        graph.computeIfAbsent(taskId, k -> new ArrayList<>()).add(dependTaskId);

        // 使用DFS检测环
        Set<Long> visited = new HashSet<>();
        Set<Long> recursionStack = new HashSet<>();

        return hasCycle(taskId, graph, visited, recursionStack);
    }

    /**
     * 查询任务DAG图数据
     */
    @Override
    public Map<String, Object> getTaskDag(Long taskId, int depth) {
        log.info("查询任务DAG图数据: taskId={}, depth={}", taskId, depth);

        // 查询任务是否存在
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 构建DAG图数据
        Map<String, Object> dagData = new HashMap<>();
        Set<Long> visitedNodes = new HashSet<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        // 递归构建DAG图
        buildDagData(taskId, depth, 0, visitedNodes, nodes, edges);

        dagData.put("nodes", nodes);
        dagData.put("edges", edges);

        return dagData;
    }

    /**
     * 构建依赖图
     */
    private void buildDependencyGraph(Map<Long, List<Long>> graph) {
        List<TaskDependency> allDependencies = taskDependencyRepository.findAll();
        for (TaskDependency dep : allDependencies) {
            graph.computeIfAbsent(dep.getTaskId(), k -> new ArrayList<>()).add(dep.getDependTaskId());
        }
    }

    /**
     * DFS检测环
     */
    private boolean hasCycle(Long taskId, Map<Long, List<Long>> graph,
                            Set<Long> visited, Set<Long> recursionStack) {
        visited.add(taskId);
        recursionStack.add(taskId);

        List<Long> neighbors = graph.get(taskId);
        if (neighbors != null) {
            for (Long neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    if (hasCycle(neighbor, graph, visited, recursionStack)) {
                        return true;
                    }
                } else if (recursionStack.contains(neighbor)) {
                    // 发现环
                    return true;
                }
            }
        }

        recursionStack.remove(taskId);
        return false;
    }

    /**
     * 递归构建DAG图数据
     */
    private void buildDagData(Long taskId, int maxDepth, int currentDepth,
                             Set<Long> visitedNodes, List<Map<String, Object>> nodes,
                             List<Map<String, Object>> edges) {
        // 如果已访问过或超过最大深度,则返回
        if (visitedNodes.contains(taskId) || (maxDepth > 0 && currentDepth >= maxDepth)) {
            return;
        }

        visitedNodes.add(taskId);

        // 查询任务信息
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            return;
        }

        Task task = taskOpt.get();

        // 添加节点
        Map<String, Object> node = new HashMap<>();
        node.put("id", task.getId());
        node.put("name", task.getTaskName());
        node.put("type", task.getTaskType());
        node.put("status", task.getStatus());
        nodes.add(node);

        // 查询上游依赖
        List<TaskDependency> upstreamDeps = taskDependencyRepository.findByTaskId(taskId);
        for (TaskDependency dep : upstreamDeps) {
            // 添加边
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", dep.getDependTaskId());
            edge.put("target", dep.getTaskId());
            edge.put("type", dep.getDependType());
            edges.add(edge);

            // 递归处理上游任务
            buildDagData(dep.getDependTaskId(), maxDepth, currentDepth + 1, visitedNodes, nodes, edges);
        }

        // 查询下游依赖
        List<TaskDependency> downstreamDeps = taskDependencyRepository.findByDependTaskId(taskId);
        for (TaskDependency dep : downstreamDeps) {
            // 添加边
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", dep.getDependTaskId());
            edge.put("target", dep.getTaskId());
            edge.put("type", dep.getDependType());
            if (!edges.contains(edge)) {
                edges.add(edge);
            }

            // 递归处理下游任务
            buildDagData(dep.getTaskId(), maxDepth, currentDepth + 1, visitedNodes, nodes, edges);
        }
    }

    /**
     * 任务参数校验
     */
    private void validateTask(Task task) {
        if (!StringUtils.hasText(task.getTaskName())) {
            throw new BusinessException("任务名称不能为空");
        }
        if (task.getTaskType() == null) {
            throw new BusinessException("任务类型不能为空");
        }
        if (!StringUtils.hasText(task.getOwner())) {
            throw new BusinessException("责任人不能为空");
        }
    }
}
