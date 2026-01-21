package com.dw.scheduler.controller;

import com.dw.scheduler.common.Result;
import com.dw.scheduler.entity.Directory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务目录管理控制器
 * 提供任务目录的CRUD操作和树形结构查询
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/directories")
@Api(tags = "任务目录管理", description = "任务目录的增删改查和树形结构管理")
public class DirectoryController {

    /**
     * 获取目录树形结构
     * 返回完整的目录层级关系，用于前端展示树形控件
     *
     * @return 目录树形结构列表
     */
    @GetMapping("/tree")
    @ApiOperation(value = "获取目录树", notes = "获取完整的目录树形结构，包含所有层级关系")
    public Result<List<Directory>> getDirectoryTree() {
        log.info("获取目录树形结构");
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 根据ID获取目录详情
     *
     * @param id 目录ID
     * @return 目录详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取目录详情", notes = "根据目录ID查询目录的详细信息")
    public Result<Directory> getDirectoryById(
            @ApiParam(value = "目录ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "目录ID不能为空") Long id) {
        log.info("查询目录详情, id: {}", id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 创建新目录
     * 支持创建多级目录结构
     *
     * @param directory 目录信息
     * @return 创建后的目录信息（包含自动生成的ID）
     */
    @PostMapping
    @ApiOperation(value = "创建目录", notes = "创建新的任务目录，支持多级目录结构")
    public Result<Directory> createDirectory(
            @ApiParam(value = "目录信息", required = true)
            @Valid @RequestBody Directory directory) {
        log.info("创建目录, directoryName: {}, parentId: {}",
                directory.getDirectoryName(), directory.getParentId());
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 更新目录信息
     * 可更新目录名称、描述等信息，不可更新父目录ID（避免循环引用）
     *
     * @param id 目录ID
     * @param directory 更新的目录信息
     * @return 更新后的目录信息
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新目录", notes = "更新目录的基本信息，如名称、描述等")
    public Result<Directory> updateDirectory(
            @ApiParam(value = "目录ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "目录ID不能为空") Long id,
            @ApiParam(value = "目录信息", required = true)
            @Valid @RequestBody Directory directory) {
        log.info("更新目录, id: {}, directoryName: {}", id, directory.getDirectoryName());
        directory.setId(id);
        // TODO: 调用Service层实现
        return Result.success();
    }

    /**
     * 删除目录
     * 逻辑删除，不物理删除数据
     * 注意：删除目录前需要检查是否存在子目录或关联任务
     *
     * @param id 目录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除目录", notes = "逻辑删除目录，删除前会检查是否存在子目录或关联任务")
    public Result<Void> deleteDirectory(
            @ApiParam(value = "目录ID", required = true, example = "1")
            @PathVariable("id") @NotNull(message = "目录ID不能为空") Long id) {
        log.info("删除目录, id: {}", id);
        // TODO: 调用Service层实现
        return Result.success();
    }
}
