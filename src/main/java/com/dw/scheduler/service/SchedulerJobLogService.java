package com.dw.scheduler.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.scheduler.entity.SchedulerJobLog;

/**
 * Scheduler Job Log Service
 *
 * @author DW Team
 * @version 1.0.0
 */
public interface SchedulerJobLogService extends IService<SchedulerJobLog> {

    /**
     * Get job log list with pagination
     *
     * @param pageNum  page number
     * @param pageSize page size
     * @param jobId    job id (optional)
     * @param status   execution status (optional)
     * @return job log list
     */
    IPage<SchedulerJobLog> getLogList(Integer pageNum, Integer pageSize, Long jobId, Integer status);

    /**
     * Save job execution log
     *
     * @param log job log
     */
    void saveLog(SchedulerJobLog log);
}
