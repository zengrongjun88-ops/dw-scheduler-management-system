package com.dw.scheduler.service;

import com.dw.scheduler.entity.Directory;

import java.util.List;

/**
 * 目录服务接口
 *
 * @author DW Team
 */
public interface DirectoryService {

    /**
     * 创建目录
     *
     * @param directory 目录信息
     * @return 创建的目录
     */
    Directory createDirectory(Directory directory);

    /**
     * 更新目录
     *
     * @param id        目录ID
     * @param directory 目录信息
     * @return 更新后的目录
     */
    Directory updateDirectory(Long id, Directory directory);

    /**
     * 删除目录
     *
     * @param id 目录ID
     */
    void deleteDirectory(Long id);

    /**
     * 查询目录树
     *
     * @param parentId 父目录ID
     * @return 目录树
     */
    List<Directory> getDirectoryTree(Long parentId);

    /**
     * 查询目录详情
     *
     * @param id 目录ID
     * @return 目录详情
     */
    Directory getDirectoryById(Long id);

    /**
     * 查询所有子目录ID(递归)
     *
     * @param directoryId 目录ID
     * @return 所有子目录ID列表
     */
    List<Long> getAllChildDirectoryIds(Long directoryId);
}
