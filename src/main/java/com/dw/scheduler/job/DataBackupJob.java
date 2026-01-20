package com.dw.scheduler.job;

import com.dw.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Backup Job - Example job for database backup
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Component
public class DataBackupJob extends BaseJob {

    @Override
    protected void executeInternal(JobExecutionContext context, SchedulerJob job) throws Exception {
        log.info("=== Data Backup Job Started ===");

        String backupTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("Backup Time: {}", backupTime);

        // Simulate backup process
        log.info("Step 1: Connecting to database...");
        Thread.sleep(1000);

        log.info("Step 2: Exporting data...");
        Thread.sleep(2000);

        log.info("Step 3: Compressing backup file...");
        Thread.sleep(1000);

        log.info("Step 4: Uploading to backup server...");
        Thread.sleep(1000);

        log.info("=== Data Backup Job Completed Successfully ===");
    }
}
