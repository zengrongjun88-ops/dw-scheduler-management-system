package com.dw.scheduler.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.scheduler.entity.SchedulerJob;

/**
 * Scheduler Job Service
 *
 * @author DW Team
 * @version 1.0.0
 */
public interface SchedulerJobService extends IService<SchedulerJob> {

    /**
     * Get job list with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @param jobName  job name (optional)
     * @param status   job status (optional)
     * @return job list
     */
    IPage<SchedulerJob> getJobList(Integer pageNum, Integer pageSize, String jobName, Integer status);

    /**
     * Create a new job
     *
     * @param job job entity
     * @return created job
     */
    SchedulerJob createJob(SchedulerJob job);

    /**
     * Update job
     *
     * @param job job entity
     * @return updated job
     */
    SchedulerJob updateJob(SchedulerJob job);

    /**
     * Delete job
     *
     * @param jobId job id
     */
    void deleteJob(Long jobId);

    /**
     * Start job
     *
     * @param jobId job id
     */
    void startJob(Long jobId);

    /**
     * Pause job
     *
     * @param jobId job id
     */
    void pauseJob(Long jobId);

    /**
     * Resume job
     *
     * @param jobId job id
     */
    void resumeJob(Long jobId);

    /**
     * Execute job immediately
     *
     * @param jobId job id
     */
    void executeJob(Long jobId);
}
