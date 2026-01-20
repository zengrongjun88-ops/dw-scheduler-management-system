package com.dw.scheduler.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.scheduler.common.ResultCode;
import com.dw.scheduler.entity.SchedulerJob;
import com.dw.scheduler.enums.JobStatusEnum;
import com.dw.scheduler.exception.BusinessException;
import com.dw.scheduler.job.BaseJob;
import com.dw.scheduler.mapper.SchedulerJobMapper;
import com.dw.scheduler.service.SchedulerJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler Job Service Implementation
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class SchedulerJobServiceImpl extends ServiceImpl<SchedulerJobMapper, SchedulerJob> implements SchedulerJobService {

    @Autowired
    private Scheduler scheduler;

    @Override
    public IPage<SchedulerJob> getJobList(Integer pageNum, Integer pageSize, String jobName, Integer status) {
        Page<SchedulerJob> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SchedulerJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(jobName), SchedulerJob::getJobName, jobName)
                .eq(status != null, SchedulerJob::getStatus, status)
                .orderByDesc(SchedulerJob::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchedulerJob createJob(SchedulerJob job) {
        // Check if job already exists
        LambdaQueryWrapper<SchedulerJob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchedulerJob::getJobName, job.getJobName())
                .eq(SchedulerJob::getJobGroup, job.getJobGroup());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ResultCode.JOB_ALREADY_EXISTS);
        }

        // Validate cron expression
        if (!CronExpression.isValidExpression(job.getCronExpression())) {
            throw new BusinessException(ResultCode.INVALID_CRON_EXPRESSION);
        }

        // Set default status to paused
        if (job.getStatus() == null) {
            job.setStatus(JobStatusEnum.PAUSED.getCode());
        }

        // Save to database
        this.save(job);

        // If status is running, start the job
        if (JobStatusEnum.RUNNING.getCode().equals(job.getStatus())) {
            try {
                addJobToScheduler(job);
            } catch (Exception e) {
                log.error("Failed to start job: {}", job.getJobName(), e);
                throw new BusinessException(ResultCode.JOB_START_FAILED, e.getMessage());
            }
        }

        return job;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchedulerJob updateJob(SchedulerJob job) {
        SchedulerJob existingJob = this.getById(job.getJobId());
        if (existingJob == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        // Validate cron expression if changed
        if (StrUtil.isNotBlank(job.getCronExpression()) &&
                !job.getCronExpression().equals(existingJob.getCronExpression())) {
            if (!CronExpression.isValidExpression(job.getCronExpression())) {
                throw new BusinessException(ResultCode.INVALID_CRON_EXPRESSION);
            }
        }

        // Update database
        this.updateById(job);

        // If job is running, reschedule it
        if (JobStatusEnum.RUNNING.getCode().equals(existingJob.getStatus())) {
            try {
                removeJobFromScheduler(existingJob);
                SchedulerJob updatedJob = this.getById(job.getJobId());
                if (JobStatusEnum.RUNNING.getCode().equals(updatedJob.getStatus())) {
                    addJobToScheduler(updatedJob);
                }
            } catch (Exception e) {
                log.error("Failed to reschedule job: {}", job.getJobName(), e);
                throw new BusinessException(ResultCode.JOB_START_FAILED, e.getMessage());
            }
        }

        return this.getById(job.getJobId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long jobId) {
        SchedulerJob job = this.getById(jobId);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        // Remove from scheduler if running
        if (JobStatusEnum.RUNNING.getCode().equals(job.getStatus())) {
            try {
                removeJobFromScheduler(job);
            } catch (Exception e) {
                log.error("Failed to remove job from scheduler: {}", job.getJobName(), e);
                throw new BusinessException(ResultCode.JOB_DELETE_FAILED, e.getMessage());
            }
        }

        // Delete from database (soft delete due to @TableLogic)
        this.removeById(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startJob(Long jobId) {
        SchedulerJob job = this.getById(jobId);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        if (JobStatusEnum.RUNNING.getCode().equals(job.getStatus())) {
            throw new BusinessException("Job is already running");
        }

        try {
            addJobToScheduler(job);
            job.setStatus(JobStatusEnum.RUNNING.getCode());
            this.updateById(job);
        } catch (Exception e) {
            log.error("Failed to start job: {}", job.getJobName(), e);
            throw new BusinessException(ResultCode.JOB_START_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseJob(Long jobId) {
        SchedulerJob job = this.getById(jobId);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        if (JobStatusEnum.PAUSED.getCode().equals(job.getStatus())) {
            throw new BusinessException("Job is already paused");
        }

        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            scheduler.pauseJob(jobKey);
            job.setStatus(JobStatusEnum.PAUSED.getCode());
            this.updateById(job);
        } catch (Exception e) {
            log.error("Failed to pause job: {}", job.getJobName(), e);
            throw new BusinessException(ResultCode.JOB_PAUSE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeJob(Long jobId) {
        SchedulerJob job = this.getById(jobId);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        if (JobStatusEnum.RUNNING.getCode().equals(job.getStatus())) {
            throw new BusinessException("Job is already running");
        }

        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            scheduler.resumeJob(jobKey);
            job.setStatus(JobStatusEnum.RUNNING.getCode());
            this.updateById(job);
        } catch (Exception e) {
            log.error("Failed to resume job: {}", job.getJobName(), e);
            throw new BusinessException(ResultCode.JOB_RESUME_FAILED, e.getMessage());
        }
    }

    @Override
    public void executeJob(Long jobId) {
        SchedulerJob job = this.getById(jobId);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        }

        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            scheduler.triggerJob(jobKey);
        } catch (Exception e) {
            log.error("Failed to execute job: {}", job.getJobName(), e);
            throw new BusinessException(ResultCode.JOB_EXECUTE_FAILED, e.getMessage());
        }
    }

    /**
     * Add job to Quartz scheduler
     */
    @SuppressWarnings("unchecked")
    private void addJobToScheduler(SchedulerJob job) throws Exception {
        // Create JobDetail
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(job.getJobClass());
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(job.getJobName(), job.getJobGroup())
                .withDescription(job.getDescription())
                .build();

        // Pass job data
        jobDetail.getJobDataMap().put("jobId", job.getJobId());
        jobDetail.getJobDataMap().put("jobData", job.getJobData());

        // Create Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getJobName(), job.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                .build();

        // Schedule job
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * Remove job from Quartz scheduler
     */
    private void removeJobFromScheduler(SchedulerJob job) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.deleteJob(jobKey);
    }
}
