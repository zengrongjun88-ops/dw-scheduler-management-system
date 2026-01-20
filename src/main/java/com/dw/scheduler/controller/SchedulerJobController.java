package com.dw.scheduler.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dw.scheduler.common.Result;
import com.dw.scheduler.entity.SchedulerJob;
import com.dw.scheduler.service.SchedulerJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * Scheduler Job Controller
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/job")
@Api(tags = "Scheduler Job Management")
@Validated
public class SchedulerJobController {

    @Autowired
    private SchedulerJobService schedulerJobService;

    @GetMapping("/list")
    @ApiOperation("Get job list with pagination")
    public Result<IPage<SchedulerJob>> getJobList(
            @ApiParam("Page number") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("Page size") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("Job name") @RequestParam(required = false) String jobName,
            @ApiParam("Job status") @RequestParam(required = false) Integer status) {
        IPage<SchedulerJob> page = schedulerJobService.getJobList(pageNum, pageSize, jobName, status);
        return Result.success(page);
    }

    @GetMapping("/{jobId}")
    @ApiOperation("Get job by ID")
    public Result<SchedulerJob> getJobById(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        SchedulerJob job = schedulerJobService.getById(jobId);
        return Result.success(job);
    }

    @PostMapping
    @ApiOperation("Create a new job")
    public Result<SchedulerJob> createJob(@RequestBody @Validated SchedulerJob job) {
        SchedulerJob createdJob = schedulerJobService.createJob(job);
        return Result.success(createdJob);
    }

    @PutMapping
    @ApiOperation("Update job")
    public Result<SchedulerJob> updateJob(@RequestBody @Validated SchedulerJob job) {
        SchedulerJob updatedJob = schedulerJobService.updateJob(job);
        return Result.success(updatedJob);
    }

    @DeleteMapping("/{jobId}")
    @ApiOperation("Delete job")
    public Result<Void> deleteJob(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        schedulerJobService.deleteJob(jobId);
        return Result.success();
    }

    @PostMapping("/{jobId}/start")
    @ApiOperation("Start job")
    public Result<Void> startJob(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        schedulerJobService.startJob(jobId);
        return Result.success();
    }

    @PostMapping("/{jobId}/pause")
    @ApiOperation("Pause job")
    public Result<Void> pauseJob(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        schedulerJobService.pauseJob(jobId);
        return Result.success();
    }

    @PostMapping("/{jobId}/resume")
    @ApiOperation("Resume job")
    public Result<Void> resumeJob(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        schedulerJobService.resumeJob(jobId);
        return Result.success();
    }

    @PostMapping("/{jobId}/execute")
    @ApiOperation("Execute job immediately")
    public Result<Void> executeJob(
            @ApiParam("Job ID") @PathVariable @NotNull Long jobId) {
        schedulerJobService.executeJob(jobId);
        return Result.success();
    }
}
