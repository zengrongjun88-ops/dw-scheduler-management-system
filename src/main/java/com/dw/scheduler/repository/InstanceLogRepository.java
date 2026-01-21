package com.dw.scheduler.repository;

import com.dw.scheduler.entity.InstanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstanceLogRepository extends JpaRepository<InstanceLog, Long> {

    /**
     * 根据实例ID查询日志列表
     */
    List<InstanceLog> findByInstanceIdOrderByLogTimeAsc(Long instanceId);

    /**
     * 根据实例ID删除日志
     */
    void deleteByInstanceId(Long instanceId);
}
