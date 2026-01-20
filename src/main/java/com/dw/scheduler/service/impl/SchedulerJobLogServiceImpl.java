package com.dw.scheduler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.scheduler.entity.SchedulerJobLog;
import com.dw.scheduler.mapper.SchedulerJobLogMapper;
import com.dw.scheduler.service.SchedulerJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Scheduler Job Log Service Implementation
 *
 * @author DW Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class SchedulerJobLogServiceImpl extends ServiceImpl<SchedulerJobLogMapper, SchedulerJobLog> implements SchedulerJobLogService {

    @Override
    public IPage<SchedulerJobLog> getLogList(Integer pageNum, Integer pageSize, Long jobId, Integer status) {
        Page<SchedulerJobLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SchedulerJobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(jobId != null, SchedulerJobLog::getJobId, jobId)
                .eq(status != null, SchedulerJobLog::getStatus, status)
                .orderByDesc(SchedulerJobLog::getStartTime);
        return this.page(page, wrapper);
    }

    @Override
    public void saveLog(SchedulerJobLog log) {
        this.save(log);
    }
}
