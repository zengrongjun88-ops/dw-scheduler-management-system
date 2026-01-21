package com.dw.scheduler.repository;

import com.dw.scheduler.entity.Datasource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, Long>, JpaSpecificationExecutor<Datasource> {

    /**
     * 根据数据源名称查询
     */
    Datasource findByDatasourceName(String datasourceName);
}
