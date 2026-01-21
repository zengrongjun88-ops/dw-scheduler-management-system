package com.dw.scheduler.service.impl;

import com.dw.scheduler.entity.InstanceLog;
import com.dw.scheduler.entity.Task;
import com.dw.scheduler.entity.TaskDependency;
import com.dw.scheduler.entity.TaskInstance;
import com.dw.scheduler.enums.InstanceStatus;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.repository.InstanceLogRepository;
import com.dw.scheduler.repository.TaskDependencyRepository;
import com.dw.scheduler.repository.TaskInstanceRepository;
import com.dw.scheduler.repository.TaskRepository;
import com.dw.scheduler.service.TaskInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;

/**
 * 任务实例服务实现类
 *
 * @author DW Team
 */
@Slf4j
@Service
public class TaskInstanceServiceImpl implements TaskInstanceService {

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private InstanceLogRepository instanceLogRepository;

    /**
     * 查询实例列表(分页和多条件搜索)
     */
    @Override
    public Page<TaskInstance> getInstanceList(String instanceName, Long taskId, InstanceStatus status,
                                              LocalDate businessDate, LocalDate startDateFrom,
                                              LocalDate startDateTo, Pageable pageable) {
        log.info("查询实例列表: instanceName={}, taskId={}, status={}, businessDate={}",
                instanceName, taskId, status, businessDate);

        // 构建动态查询条件
        Specification<TaskInstance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 实例名称模糊查询
            if (StringUtils.hasText(instanceName)) {
                predicates.add(cb.like(root.get("instanceName"), "%" + instanceName + "%"));
            }

            // 任务ID
            if (taskId != null) {
                predicates.add(cb.equal(root.get("taskId"), taskId));
            }

            // 实例状态
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 业务日期
            if (businessDate != null) {
                predicates.add(cb.equal(root.get("businessDate"), businessDate));
            }

            // 开始时间范围
            if (startDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startDateFrom.atStartOfDay()));
            }
            if (startDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), startDateTo.plusDays(1).atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return taskInstanceRepository.findAll(spec, pageable);
    }

    /**
     * 查询实例详情
     */
    @Override
    public TaskInstance getInstanceById(Long id) {
        log.info("查询实例详情: id={}", id);

        return taskInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("实例不存在"));
    }

    /**
     * 查询实例日志
     */
    @Override
    public List<InstanceLog> getInstanceLogs(Long instanceId) {
        log.info("查询实例日志: instanceId={}", instanceId);

        // 检查实例是否存在
        TaskInstance instance = taskInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("实例不存在"));

        return instanceLogRepository.findByInstanceIdOrderByLogTimeAsc(instanceId);
    }

    /**
     * 查询实例DAG图数据
     */
    @Override
    public Map<String, Object> getInstanceDag(Long instanceId) {
        log.info("查询实例DAG图数据: instanceId={}", instanceId);

        // 查询实例是否存在
        TaskInstance instance = taskInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("实例不存在"));

        // 查询该业务日期下的所有相关实例
        LocalDate businessDate = instance.getBusinessDate();
        Long taskId = instance.getTaskId();

        // 构建DAG图数据
        Map<String, Object> dagData = new HashMap<>();
        Set<Long> visitedTaskIds = new HashSet<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        // 递归构建DAG图
        buildInstanceDagData(taskId, businessDate, visitedTaskIds, nodes, edges);

        dagData.put("nodes", nodes);
        dagData.put("edges", edges);
        dagData.put("currentInstanceId", instanceId);

        return dagData;
    }

    /**
     * 创建实例(预留接口)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createInstance(TaskInstance instance) {
        log.info("创建实例: taskId={}, businessDate={}", instance.getTaskId(), instance.getBusinessDate());

        // 参数校验
        if (instance.getTaskId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        if (instance.getBusinessDate() == null) {
            throw new BusinessException("业务日期不能为空");
        }

        // 检查任务是否存在
        Task task = taskRepository.findById(instance.getTaskId())
                .orElseThrow(() -> new BusinessException("任务不存在"));

        // 检查该任务的业务日期是否已有实例
        List<TaskInstance> existInstances = taskInstanceRepository.findByTaskIdAndBusinessDate(
                instance.getTaskId(), instance.getBusinessDate());
        if (!existInstances.isEmpty()) {
            throw new BusinessException("该任务在该业务日期下已存在实例");
        }

        // 设置实例名称
        if (!StringUtils.hasText(instance.getInstanceName())) {
            String instanceName = String.format("%s_%s",
                    task.getTaskName(), instance.getBusinessDate().toString());
            instance.setInstanceName(instanceName);
        }

        // 设置默认状态
        if (instance.getStatus() == null) {
            instance.setStatus(InstanceStatus.WAITING);
        }

        // 保存任务快照(可选)
        // instance.setTaskSnapshot(JsonUtil.toJson(task));

        return taskInstanceRepository.save(instance);
    }

    /**
     * 递归构建实例DAG图数据
     */
    private void buildInstanceDagData(Long taskId, LocalDate businessDate,
                                     Set<Long> visitedTaskIds,
                                     List<Map<String, Object>> nodes,
                                     List<Map<String, Object>> edges) {
        // 如果已访问过,则返回
        if (visitedTaskIds.contains(taskId)) {
            return;
        }

        visitedTaskIds.add(taskId);

        // 查询任务信息
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            return;
        }

        Task task = taskOpt.get();

        // 查询该任务在该业务日期下的实例
        List<TaskInstance> instances = taskInstanceRepository.findByTaskIdAndBusinessDate(taskId, businessDate);
        TaskInstance instance = instances.isEmpty() ? null : instances.get(0);

        // 添加节点
        Map<String, Object> node = new HashMap<>();
        node.put("taskId", task.getId());
        node.put("taskName", task.getTaskName());
        node.put("taskType", task.getTaskType());

        if (instance != null) {
            node.put("instanceId", instance.getId());
            node.put("instanceName", instance.getInstanceName());
            node.put("status", instance.getStatus());
            node.put("startTime", instance.getStartTime());
            node.put("endTime", instance.getEndTime());
            node.put("executeTime", instance.getExecuteTime());
        } else {
            node.put("instanceId", null);
            node.put("status", "NOT_CREATED");
        }

        nodes.add(node);

        // 查询上游依赖
        List<TaskDependency> upstreamDeps = taskDependencyRepository.findByTaskId(taskId);
        for (TaskDependency dep : upstreamDeps) {
            // 添加边
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", dep.getDependTaskId());
            edge.put("target", dep.getTaskId());
            edge.put("type", dep.getDependType());
            edge.put("cycleOffset", dep.getCycleOffset());
            edges.add(edge);

            // 计算依赖任务的业务日期(考虑周期偏移)
            LocalDate dependBusinessDate = businessDate.plusDays(dep.getCycleOffset());

            // 递归处理上游任务
            buildInstanceDagData(dep.getDependTaskId(), dependBusinessDate, visitedTaskIds, nodes, edges);
        }

        // 查询下游依赖
        List<TaskDependency> downstreamDeps = taskDependencyRepository.findByDependTaskId(taskId);
        for (TaskDependency dep : downstreamDeps) {
            // 添加边
            Map<String, Object> edge = new HashMap<>();
            edge.put("source", dep.getDependTaskId());
            edge.put("target", dep.getTaskId());
            edge.put("type", dep.getDependType());
            edge.put("cycleOffset", dep.getCycleOffset());
            if (!edges.contains(edge)) {
                edges.add(edge);
            }

            // 计算下游任务的业务日期(考虑周期偏移)
            LocalDate downstreamBusinessDate = businessDate.minusDays(dep.getCycleOffset());

            // 递归处理下游任务
            buildInstanceDagData(dep.getTaskId(), downstreamBusinessDate, visitedTaskIds, nodes, edges);
        }
    }
}
