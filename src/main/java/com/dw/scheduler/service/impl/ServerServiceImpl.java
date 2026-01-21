package com.dw.scheduler.service.impl;

import com.dw.scheduler.entity.Server;
import com.dw.scheduler.enums.ServerRole;
import com.dw.scheduler.enums.ServerStatus;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.repository.ServerRepository;
import com.dw.scheduler.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器服务实现类
 *
 * @author DW Team
 */
@Slf4j
@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private ServerRepository serverRepository;

    /**
     * 注册服务器
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Server registerServer(Server server) {
        log.info("注册服务器: {}", server.getIpAddress());

        // 参数校验
        validateServer(server);

        // 检查IP地址是否已存在
        Server existServer = serverRepository.findByIpAddress(server.getIpAddress());
        if (existServer != null) {
            // 如果服务器已存在,更新信息
            log.info("服务器已存在,更新信息: {}", server.getIpAddress());
            updateServerInfo(existServer, server);
            return serverRepository.save(existServer);
        }

        // 设置默认值
        if (server.getStatus() == null) {
            server.setStatus(ServerStatus.ONLINE);
        }
        if (server.getCurrentTaskNum() == null) {
            server.setCurrentTaskNum(0);
        }
        if (server.getMaxTaskNum() == null) {
            server.setMaxTaskNum(100);
        }

        // 设置最后心跳时间
        server.setLastHeartbeat(LocalDateTime.now());

        return serverRepository.save(server);
    }

    /**
     * 更新服务器信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Server updateServer(Long id, Server server) {
        log.info("更新服务器: id={}, ip={}", id, server.getIpAddress());

        // 查询服务器是否存在
        Server existServer = serverRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务器不存在"));

        // 参数校验
        validateServer(server);

        // 检查IP地址是否重复(排除自己)
        Server sameIpServer = serverRepository.findByIpAddress(server.getIpAddress());
        if (sameIpServer != null && !sameIpServer.getId().equals(id)) {
            throw new BusinessException("IP地址已存在");
        }

        // 更新服务器信息
        updateServerInfo(existServer, server);

        return serverRepository.save(existServer);
    }

    /**
     * 删除服务器
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServer(Long id) {
        log.info("删除服务器: id={}", id);

        // 查询服务器是否存在
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务器不存在"));

        // 检查服务器状态,不能删除在线服务器
        if (server.getStatus() == ServerStatus.ONLINE) {
            throw new BusinessException("服务器在线中,无法删除");
        }

        // 检查是否有正在运行的任务
        if (server.getCurrentTaskNum() != null && server.getCurrentTaskNum() > 0) {
            throw new BusinessException("服务器上有正在运行的任务,无法删除");
        }

        // 逻辑删除
        server.setDeleted(1);
        serverRepository.save(server);
    }

    /**
     * 查询服务器列表(分页)
     */
    @Override
    public Page<Server> getServerList(ServerRole serverRole, String resourceGroup,
                                      ServerStatus status, Pageable pageable) {
        log.info("查询服务器列表: serverRole={}, resourceGroup={}, status={}",
                serverRole, resourceGroup, status);

        // 构建动态查询条件
        Specification<Server> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 服务器角色
            if (serverRole != null) {
                predicates.add(cb.equal(root.get("serverRole"), serverRole));
            }

            // 资源分组
            if (StringUtils.hasText(resourceGroup)) {
                predicates.add(cb.equal(root.get("resourceGroup"), resourceGroup));
            }

            // 服务器状态
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return serverRepository.findAll(spec, pageable);
    }

    /**
     * 查询服务器详情
     */
    @Override
    public Server getServerById(Long id) {
        log.info("查询服务器详情: id={}", id);

        return serverRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务器不存在"));
    }

    /**
     * 根据IP地址查询服务器
     */
    @Override
    public Server getServerByIpAddress(String ipAddress) {
        log.info("根据IP地址查询服务器: ipAddress={}", ipAddress);

        if (!StringUtils.hasText(ipAddress)) {
            throw new BusinessException("IP地址不能为空");
        }

        Server server = serverRepository.findByIpAddress(ipAddress);
        if (server == null) {
            throw new BusinessException("服务器不存在");
        }

        return server;
    }

    /**
     * 查询在线Worker节点
     */
    @Override
    public List<Server> getOnlineWorkers() {
        log.info("查询在线Worker节点");

        return serverRepository.findByServerRoleAndStatus(ServerRole.WORKER, ServerStatus.ONLINE);
    }

    /**
     * 服务器参数校验
     */
    private void validateServer(Server server) {
        if (!StringUtils.hasText(server.getIpAddress())) {
            throw new BusinessException("IP地址不能为空");
        }
        if (server.getServerRole() == null) {
            throw new BusinessException("服务器角色不能为空");
        }
    }

    /**
     * 更新服务器信息(内部方法)
     */
    private void updateServerInfo(Server existServer, Server newServer) {
        existServer.setServerName(newServer.getServerName());
        existServer.setIpAddress(newServer.getIpAddress());
        existServer.setServerRole(newServer.getServerRole());
        existServer.setResourceGroup(newServer.getResourceGroup());

        if (newServer.getStatus() != null) {
            existServer.setStatus(newServer.getStatus());
        }
        if (newServer.getCpuCores() != null) {
            existServer.setCpuCores(newServer.getCpuCores());
        }
        if (newServer.getMemorySize() != null) {
            existServer.setMemorySize(newServer.getMemorySize());
        }
        if (newServer.getDiskSize() != null) {
            existServer.setDiskSize(newServer.getDiskSize());
        }
        if (newServer.getMaxTaskNum() != null) {
            existServer.setMaxTaskNum(newServer.getMaxTaskNum());
        }
        if (newServer.getCurrentTaskNum() != null) {
            existServer.setCurrentTaskNum(newServer.getCurrentTaskNum());
        }
        if (newServer.getCpuUsage() != null) {
            existServer.setCpuUsage(newServer.getCpuUsage());
        }
        if (newServer.getMemoryUsage() != null) {
            existServer.setMemoryUsage(newServer.getMemoryUsage());
        }
        if (newServer.getDiskUsage() != null) {
            existServer.setDiskUsage(newServer.getDiskUsage());
        }

        // 更新最后心跳时间
        existServer.setLastHeartbeat(LocalDateTime.now());
    }
}
