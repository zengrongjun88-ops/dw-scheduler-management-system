package com.dw.scheduler.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dw.scheduler.common.Result;
import com.dw.scheduler.entity.SchedulerJobLog;
import com.dw.scheduler.service.SchedulerJobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Scheduler Job Log Controller
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/log")
@Api(tags = "Scheduler Job Log Management")
@Validated
public class SchedulerJobLogController {

    @Autowired
    private SchedulerJobLogService schedulerJobLogService;

    @GetMapping("/list")
    @ApiOperation("Get job log list with pagination")
    public Result<IPage<SchedulerJobLog>> getLogList(
            @ApiParam("Page number") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("Page size") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("Job ID") @RequestParam(required = false) Long jobId,
            @ApiParam("Execution status") @RequestParam(required = false) Integer status) {
        IPage<SchedulerJobLog> page = schedulerJobLogService.getLogList(pageNum, pageSize, jobId, status);
        return Result.success(page);
    }

    @GetMapping("/{logId}")
    @ApiOperation("Get job log by ID")
    public Result<SchedulerJobLog> getLogById(
            @ApiParam("Log ID") @PathVariable @NotNull Long logId) {
        SchedulerJobLog log = schedulerJobLogService.getById(logId);
        return Result.success(log);
    }
}
