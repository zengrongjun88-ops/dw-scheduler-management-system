package com.dw.scheduler.service;

import com.dw.scheduler.entity.Server;
import com.dw.scheduler.enums.ServerRole;
import com.dw.scheduler.enums.ServerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 服务器服务接口
 *
 * @author DW Team
 */
public interface ServerService {

    /**
     * 注册服务器
     *
     * @param server 服务器信息
     * @return 注册的服务器
     */
    Server registerServer(Server server);

    /**
     * 更新服务器信息
     *
     * @param id     服务器ID
     * @param server 服务器信息
     * @return 更新后的服务器
     */
    Server updateServer(Long id, Server server);

    /**
     * 删除服务器
     *
     * @param id 服务器ID
     */
    void deleteServer(Long id);

    /**
     * 查询服务器列表(分页)
     *
     * @param serverRole    服务器角色
     * @param resourceGroup 资源分组
     * @param status        服务器状态
     * @param pageable      分页参数
     * @return 服务器分页列表
     */
    Page<Server> getServerList(ServerRole serverRole, String resourceGroup,
                               ServerStatus status, Pageable pageable);

    /**
     * 查询服务器详情
     *
     * @param id 服务器ID
     * @return 服务器详情
     */
    Server getServerById(Long id);

    /**
     * 根据IP地址查询服务器
     *
     * @param ipAddress IP地址
     * @return 服务器信息
     */
    Server getServerByIpAddress(String ipAddress);

    /**
     * 查询在线Worker节点
     *
     * @return 在线Worker列表
     */
    List<Server> getOnlineWorkers();
}
