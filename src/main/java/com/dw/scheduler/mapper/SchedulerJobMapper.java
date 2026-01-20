package com.dw.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dw.scheduler.entity.SchedulerJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * Scheduler Job Mapper
 *
 * @author DW Team
 * @version 1.0.0
 */
@Mapper
public interface SchedulerJobMapper extends BaseMapper<SchedulerJob> {
}
