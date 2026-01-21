package com.dw.scheduler.service.impl;

import com.dw.scheduler.entity.Datasource;
import com.dw.scheduler.entity.Task;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.repository.DatasourceRepository;
import com.dw.scheduler.repository.TaskRepository;
import com.dw.scheduler.service.DatasourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据源服务实现类
 *
 * @author DW Team
 */
@Slf4j
@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * 创建数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Datasource createDatasource(Datasource datasource) {
        log.info("创建数据源: {}", datasource.getDatasourceName());

        // 参数校验
        validateDatasource(datasource);

        // 检查数据源名称是否已存在
        Datasource existDatasource = datasourceRepository.findByDatasourceName(datasource.getDatasourceName());
        if (existDatasource != null) {
            throw new BusinessException("数据源名称已存在");
        }

        return datasourceRepository.save(datasource);
    }

    /**
     * 更新数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Datasource updateDatasource(Long id, Datasource datasource) {
        log.info("更新数据源: id={}, name={}", id, datasource.getDatasourceName());

        // 查询数据源是否存在
        Datasource existDatasource = datasourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("数据源不存在"));

        // 参数校验
        validateDatasource(datasource);

        // 检查数据源名称是否重复(排除自己)
        Datasource sameNameDatasource = datasourceRepository.findByDatasourceName(datasource.getDatasourceName());
        if (sameNameDatasource != null && !sameNameDatasource.getId().equals(id)) {
            throw new BusinessException("数据源名称已存在");
        }

        // 更新数据源信息
        existDatasource.setDatasourceName(datasource.getDatasourceName());
        existDatasource.setDatasourceType(datasource.getDatasourceType());
        existDatasource.setJdbcUrl(datasource.getJdbcUrl());
        existDatasource.setUsername(datasource.getUsername());
        existDatasource.setPassword(datasource.getPassword());
        existDatasource.setDriverClass(datasource.getDriverClass());
        existDatasource.setDescription(datasource.getDescription());
        existDatasource.setUpdateBy(datasource.getUpdateBy());

        return datasourceRepository.save(existDatasource);
    }

    /**
     * 删除数据源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDatasource(Long id) {
        log.info("删除数据源: id={}", id);

        // 查询数据源是否存在
        Datasource datasource = datasourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("数据源不存在"));

        // 检查是否有任务在使用该数据源
        List<Task> tasks = taskRepository.findAll((root, query, cb) ->
                cb.equal(root.get("datasourceId"), id));
        if (!tasks.isEmpty()) {
            throw new BusinessException("该数据源正在被任务使用,无法删除");
        }

        // 逻辑删除
        datasource.setDeleted(1);
        datasourceRepository.save(datasource);
    }

    /**
     * 查询数据源列表(分页)
     */
    @Override
    public Page<Datasource> getDatasourceList(String datasourceName, String datasourceType, Pageable pageable) {
        log.info("查询数据源列表: datasourceName={}, datasourceType={}", datasourceName, datasourceType);

        // 构建动态查询条件
        Specification<Datasource> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 数据源名称模糊查询
            if (StringUtils.hasText(datasourceName)) {
                predicates.add(cb.like(root.get("datasourceName"), "%" + datasourceName + "%"));
            }

            // 数据源类型
            if (StringUtils.hasText(datasourceType)) {
                predicates.add(cb.equal(root.get("datasourceType"), datasourceType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return datasourceRepository.findAll(spec, pageable);
    }

    /**
     * 查询数据源详情
     */
    @Override
    public Datasource getDatasourceById(Long id) {
        log.info("查询数据源详情: id={}", id);

        return datasourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("数据源不存在"));
    }

    /**
     * 测试数据源连接(已保存的数据源)
     */
    @Override
    public String testConnection(Long id) {
        log.info("测试数据源连接: id={}", id);

        Datasource datasource = datasourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("数据源不存在"));

        return testConnection(datasource);
    }

    /**
     * 测试数据源连接(不保存)
     */
    @Override
    public String testConnection(Datasource datasource) {
        log.info("测试数据源连接: {}", datasource.getDatasourceName());

        // 参数校验
        if (!StringUtils.hasText(datasource.getJdbcUrl())) {
            throw new BusinessException("JDBC URL不能为空");
        }
        if (!StringUtils.hasText(datasource.getDriverClass())) {
            throw new BusinessException("驱动类不能为空");
        }

        Connection conn = null;
        try {
            // 加载驱动
            Class.forName(datasource.getDriverClass());

            // 建立连接
            long startTime = System.currentTimeMillis();
            conn = DriverManager.getConnection(
                    datasource.getJdbcUrl(),
                    datasource.getUsername(),
                    datasource.getPassword()
            );
            long endTime = System.currentTimeMillis();

            // 测试连接是否有效
            if (conn != null && !conn.isClosed()) {
                String message = String.format("连接成功! 耗时: %d ms", (endTime - startTime));
                log.info("数据源 [{}] 测试连接成功, 耗时: {} ms",
                        datasource.getDatasourceName(), (endTime - startTime));
                return message;
            } else {
                throw new BusinessException("连接失败: 无法建立有效连接");
            }

        } catch (ClassNotFoundException e) {
            String errorMsg = "驱动类不存在: " + datasource.getDriverClass();
            log.error("数据源 [{}] 测试连接失败: {}", datasource.getDatasourceName(), errorMsg, e);
            throw new BusinessException(errorMsg);
        } catch (SQLException e) {
            String errorMsg = "数据库连接失败: " + e.getMessage();
            log.error("数据源 [{}] 测试连接失败: {}", datasource.getDatasourceName(), errorMsg, e);
            throw new BusinessException(errorMsg);
        } finally {
            // 关闭连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("关闭数据库连接失败", e);
                }
            }
        }
    }

    /**
     * 数据源参数校验
     */
    private void validateDatasource(Datasource datasource) {
        if (!StringUtils.hasText(datasource.getDatasourceName())) {
            throw new BusinessException("数据源名称不能为空");
        }
        if (!StringUtils.hasText(datasource.getDatasourceType())) {
            throw new BusinessException("数据源类型不能为空");
        }
        if (!StringUtils.hasText(datasource.getJdbcUrl())) {
            throw new BusinessException("JDBC URL不能为空");
        }
        if (!StringUtils.hasText(datasource.getDriverClass())) {
            throw new BusinessException("驱动类不能为空");
        }
    }
}
