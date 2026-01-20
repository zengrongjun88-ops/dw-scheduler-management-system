package com.dw.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dw.scheduler.entity.SchedulerJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Scheduler Job Log Mapper
 *
 * @author DW Team
 * @version 1.0.0
 */
@Mapper
public interface SchedulerJobLogMapper extends BaseMapper<SchedulerJobLog> {
}
