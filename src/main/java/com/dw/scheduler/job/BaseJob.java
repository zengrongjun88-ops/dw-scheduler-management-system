package com.dw.scheduler.job;

import com.dw.scheduler.entity.SchedulerJob;
import com.dw.scheduler.entity.SchedulerJobLog;
import com.dw.scheduler.enums.LogStatusEnum;
import com.dw.scheduler.service.SchedulerJobLogService;
import com.dw.scheduler.service.SchedulerJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * Base Job Class
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
public abstract class BaseJob implements Job {

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Autowired
    private SchedulerJobLogService schedulerJobLogService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");
        SchedulerJob job = schedulerJobService.getById(jobId);

        if (job == null) {
            log.error("Job not found: {}", jobId);
            return;
        }

        // Create log record
        SchedulerJobLog jobLog = new SchedulerJobLog();
        jobLog.setJobId(job.getJobId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setStartTime(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        try {
            log.info("Job started: {} - {}", job.getJobName(), job.getJobGroup());

            // Execute job logic
            executeInternal(context, job);

            // Log success
            long endTime = System.currentTimeMillis();
            jobLog.setEndTime(LocalDateTime.now());
            jobLog.setExecuteTime(endTime - startTime);
            jobLog.setStatus(LogStatusEnum.SUCCESS.getCode());
            jobLog.setMessage("Job executed successfully");

            log.info("Job completed: {} - {} ({}ms)", job.getJobName(), job.getJobGroup(), endTime - startTime);

        } catch (Exception e) {
            log.error("Job execution failed: {} - {}", job.getJobName(), job.getJobGroup(), e);

            // Log failure
            long endTime = System.currentTimeMillis();
            jobLog.setEndTime(LocalDateTime.now());
            jobLog.setExecuteTime(endTime - startTime);
            jobLog.setStatus(LogStatusEnum.FAILED.getCode());
            jobLog.setMessage("Job execution failed: " + e.getMessage());
            jobLog.setExceptionInfo(getStackTrace(e));

            throw new JobExecutionException(e);
        } finally {
            // Save log to database
            schedulerJobLogService.saveLog(jobLog);
        }
    }

    /**
     * Execute job logic (to be implemented by subclasses)
     *
     * @param context job execution context
     * @param job     scheduler job
     * @throws Exception if job execution fails
     */
    protected abstract void executeInternal(JobExecutionContext context, SchedulerJob job) throws Exception;

    /**
     * Get stack trace as string
     */
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
