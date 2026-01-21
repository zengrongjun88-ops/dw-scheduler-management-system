package com.dw.scheduler.repository;

import com.dw.scheduler.entity.Server;
import com.dw.scheduler.enums.ServerRole;
import com.dw.scheduler.enums.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long>, JpaSpecificationExecutor<Server> {

    /**
     * 根据IP地址查询服务器
     */
    Server findByIpAddress(String ipAddress);

    /**
     * 根据服务器角色查询
     */
    List<Server> findByServerRole(ServerRole serverRole);

    /**
     * 根据状态查询服务器列表
     */
    List<Server> findByStatus(ServerStatus status);

    /**
     * 根据资源分组查询
     */
    List<Server> findByResourceGroup(String resourceGroup);

    /**
     * 查询在线的Worker节点
     */
    List<Server> findByServerRoleAndStatus(ServerRole serverRole, ServerStatus status);
}
