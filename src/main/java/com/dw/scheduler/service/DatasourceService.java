package com.dw.scheduler.service;

import com.dw.scheduler.entity.Datasource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 数据源服务接口
 *
 * @author DW Team
 */
public interface DatasourceService {

    /**
     * 创建数据源
     *
     * @param datasource 数据源信息
     * @return 创建的数据源
     */
    Datasource createDatasource(Datasource datasource);

    /**
     * 更新数据源
     *
     * @param id         数据源ID
     * @param datasource 数据源信息
     * @return 更新后的数据源
     */
    Datasource updateDatasource(Long id, Datasource datasource);

    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void deleteDatasource(Long id);

    /**
     * 查询数据源列表(分页)
     *
     * @param datasourceName 数据源名称(模糊查询)
     * @param datasourceType 数据源类型
     * @param pageable       分页参数
     * @return 数据源分页列表
     */
    Page<Datasource> getDatasourceList(String datasourceName, String datasourceType, Pageable pageable);

    /**
     * 查询数据源详情
     *
     * @param id 数据源ID
     * @return 数据源详情
     */
    Datasource getDatasourceById(Long id);

    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 测试结果信息
     */
    String testConnection(Long id);

    /**
     * 测试数据源连接(不保存)
     *
     * @param datasource 数据源信息
     * @return 测试结果信息
     */
    String testConnection(Datasource datasource);
}
