package com.dw.scheduler.service.impl;

import com.dw.scheduler.entity.Directory;
import com.dw.scheduler.entity.Task;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.repository.DirectoryRepository;
import com.dw.scheduler.repository.TaskRepository;
import com.dw.scheduler.service.DirectoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录服务实现类
 *
 * @author DW Team
 */
@Slf4j
@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * 创建目录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Directory createDirectory(Directory directory) {
        log.info("创建目录: {}", directory.getDirectoryName());

        // 参数校验
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            throw new BusinessException("目录名称不能为空");
        }

        // 检查同一父目录下是否存在同名目录
        Directory existDirectory = directoryRepository.findByDirectoryNameAndParentId(
                directory.getDirectoryName(), directory.getParentId());
        if (existDirectory != null) {
            throw new BusinessException("同一目录下已存在相同名称的目录");
        }

        // 构建目录路径
        String directoryPath = buildDirectoryPath(directory.getParentId(), directory.getDirectoryName());
        directory.setDirectoryPath(directoryPath);

        return directoryRepository.save(directory);
    }

    /**
     * 更新目录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Directory updateDirectory(Long id, Directory directory) {
        log.info("更新目录: id={}, name={}", id, directory.getDirectoryName());

        // 查询目录是否存在
        Directory existDirectory = directoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("目录不存在"));

        // 参数校验
        if (!StringUtils.hasText(directory.getDirectoryName())) {
            throw new BusinessException("目录名称不能为空");
        }

        // 检查同一父目录下是否存在同名目录(排除自己)
        Directory sameNameDirectory = directoryRepository.findByDirectoryNameAndParentId(
                directory.getDirectoryName(), directory.getParentId());
        if (sameNameDirectory != null && !sameNameDirectory.getId().equals(id)) {
            throw new BusinessException("同一目录下已存在相同名称的目录");
        }

        // 更新目录信息
        existDirectory.setDirectoryName(directory.getDirectoryName());
        existDirectory.setDescription(directory.getDescription());
        existDirectory.setOwner(directory.getOwner());
        existDirectory.setUpdateBy(directory.getUpdateBy());

        // 重新构建目录路径
        String directoryPath = buildDirectoryPath(existDirectory.getParentId(), existDirectory.getDirectoryName());
        existDirectory.setDirectoryPath(directoryPath);

        Directory saved = directoryRepository.save(existDirectory);

        // 更新所有子目录的路径
        updateChildDirectoryPaths(id);

        return saved;
    }

    /**
     * 删除目录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDirectory(Long id) {
        log.info("删除目录: id={}", id);

        // 查询目录是否存在
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("目录不存在"));

        // 检查是否存在子目录
        List<Directory> childDirectories = directoryRepository.findByParentIdOrderByCreateTimeAsc(id);
        if (!childDirectories.isEmpty()) {
            throw new BusinessException("该目录下存在子目录,无法删除");
        }

        // 检查目录下是否存在任务
        List<Task> tasks = taskRepository.findByDirectoryId(id);
        if (!tasks.isEmpty()) {
            throw new BusinessException("该目录下存在任务,无法删除");
        }

        // 逻辑删除
        directory.setDeleted(1);
        directoryRepository.save(directory);
    }

    /**
     * 查询目录树
     */
    @Override
    public List<Directory> getDirectoryTree(Long parentId) {
        log.info("查询目录树: parentId={}", parentId);

        if (parentId == null) {
            parentId = 0L;
        }

        return directoryRepository.findByParentIdOrderByCreateTimeAsc(parentId);
    }

    /**
     * 查询目录详情
     */
    @Override
    public Directory getDirectoryById(Long id) {
        log.info("查询目录详情: id={}", id);

        return directoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("目录不存在"));
    }

    /**
     * 查询所有子目录ID(递归)
     */
    @Override
    public List<Long> getAllChildDirectoryIds(Long directoryId) {
        log.info("递归查询所有子目录ID: directoryId={}", directoryId);

        List<Long> result = new ArrayList<>();
        collectChildDirectoryIds(directoryId, result);
        return result;
    }

    /**
     * 递归收集子目录ID
     */
    private void collectChildDirectoryIds(Long parentId, List<Long> result) {
        List<Directory> children = directoryRepository.findByParentIdOrderByCreateTimeAsc(parentId);
        for (Directory child : children) {
            result.add(child.getId());
            // 递归查询子目录的子目录
            collectChildDirectoryIds(child.getId(), result);
        }
    }

    /**
     * 构建目录路径
     */
    private String buildDirectoryPath(Long parentId, String directoryName) {
        if (parentId == null || parentId == 0L) {
            return "/" + directoryName;
        }

        Directory parentDirectory = directoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException("父目录不存在"));

        String parentPath = parentDirectory.getDirectoryPath();
        if (!StringUtils.hasText(parentPath)) {
            parentPath = "/" + parentDirectory.getDirectoryName();
        }

        return parentPath + "/" + directoryName;
    }

    /**
     * 更新所有子目录的路径(递归)
     */
    private void updateChildDirectoryPaths(Long parentId) {
        List<Directory> children = directoryRepository.findByParentIdOrderByCreateTimeAsc(parentId);
        for (Directory child : children) {
            String newPath = buildDirectoryPath(parentId, child.getDirectoryName());
            child.setDirectoryPath(newPath);
            directoryRepository.save(child);

            // 递归更新子目录的子目录
            updateChildDirectoryPaths(child.getId());
        }
    }
}
