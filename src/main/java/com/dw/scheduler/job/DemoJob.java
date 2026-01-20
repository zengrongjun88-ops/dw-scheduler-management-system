package com.dw.scheduler.job;

import com.dw.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Demo Job - Example job that runs every 30 seconds
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class DemoJob extends BaseJob {

    @Override
    protected void executeInternal(JobExecutionContext context, SchedulerJob job) throws Exception {
        log.info("=== Demo Job Executing ===");
        log.info("Job Name: {}", job.getJobName());
        log.info("Job Group: {}", job.getJobGroup());
        log.info("Cron Expression: {}", job.getCronExpression());
        log.info("Job Data: {}", job.getJobData());

        // Simulate some work
        Thread.sleep(2000);

        log.info("=== Demo Job Completed ===");
    }
}
