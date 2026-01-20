# DW Scheduler Management System

## 项目简介

DW Scheduler Management System (DW调度管理系统) 是一个基于Spring Boot + Quartz的分布式任务调度管理系统。该系统提供了完整的任务调度功能，包括任务的增删改查、启动/暂停/恢复、立即执行、执行日志记录等功能。

## 技术栈

- **核心框架**: Spring Boot 2.7.18
- **持久层**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **调度框架**: Quartz 2.3.2
- **连接池**: Druid 1.2.20
- **API文档**: Knife4j 3.0.3
- **工具类**: Hutool 5.8.22, Lombok

## 核心功能

### 1. 任务管理
- ✅ 创建任务：支持配置任务名称、分组、执行类、Cron表达式等
- ✅ 修改任务：支持修改任务配置，自动重新调度
- ✅ 删除任务：支持删除任务，自动从调度器中移除
- ✅ 查询任务：支持分页查询、条件筛选

### 2. 任务控制
- ✅ 启动任务：将任务添加到Quartz调度器中
- ✅ 暂停任务：暂停正在运行的任务
- ✅ 恢复任务：恢复已暂停的任务
- ✅ 立即执行：立即触发任务执行一次

### 3. 执行日志
- ✅ 自动记录：任务执行时自动记录日志
- ✅ 详细信息：记录执行状态、开始/结束时间、执行时长、异常信息等
- ✅ 日志查询：支持分页查询、按任务ID/状态筛选

### 4. 系统特性
- ✅ RESTful API：标准的REST接口设计
- ✅ 统一返回：统一的响应结果封装
- ✅ 全局异常处理：友好的错误提示
- ✅ 接口文档：Knife4j自动生成API文档
- ✅ 数据库监控：Druid提供数据库连接池监控

## 项目结构

```
dw-scheduler-management-system
├── src/main/java/com/dw/scheduler
│   ├── controller       # 控制层
│   │   ├── SchedulerJobController.java
│   │   └── SchedulerJobLogController.java
│   ├── service         # 服务层
│   │   ├── SchedulerJobService.java
│   │   ├── SchedulerJobLogService.java
│   │   └── impl/
│   ├── mapper          # 数据访问层
│   │   ├── SchedulerJobMapper.java
│   │   └── SchedulerJobLogMapper.java
│   ├── entity          # 实体类
│   │   ├── SchedulerJob.java
│   │   └── SchedulerJobLog.java
│   ├── job             # 调度任务
│   │   ├── BaseJob.java
│   │   ├── DemoJob.java
│   │   ├── DataBackupJob.java
│   │   └── ReportGenerationJob.java
│   ├── config          # 配置类
│   │   ├── Knife4jConfig.java
│   │   ├── MybatisPlusConfig.java
│   │   └── QuartzConfig.java
│   ├── common          # 公共类
│   │   ├── Result.java
│   │   └── ResultCode.java
│   ├── exception       # 异常处理
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── enums           # 枚举类
│   │   ├── JobStatusEnum.java
│   │   └── LogStatusEnum.java
│   └── SchedulerApplication.java  # 启动类
├── src/main/resources
│   ├── application.yml   # 配置文件
│   └── schema.sql        # 数据库初始化脚本
└── pom.xml               # Maven配置
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p

# 2. 执行初始化脚本
mysql -u root -p < src/main/resources/schema.sql
```

或者直接在MySQL客户端中执行 `src/main/resources/schema.sql` 文件。

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scheduler_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password
```

### 4. 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar target/dw-scheduler-management-system-1.0.0.jar
```

### 5. 访问系统

启动成功后，访问以下地址：

- **API文档**: http://localhost:8080/api/doc.html
- **Druid监控**: http://localhost:8080/api/druid/ (用户名/密码: admin/admin)

## API接口

### 任务管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/job/list | GET | 获取任务列表（分页） |
| /api/job/{jobId} | GET | 根据ID获取任务详情 |
| /api/job | POST | 创建新任务 |
| /api/job | PUT | 更新任务 |
| /api/job/{jobId} | DELETE | 删除任务 |
| /api/job/{jobId}/start | POST | 启动任务 |
| /api/job/{jobId}/pause | POST | 暂停任务 |
| /api/job/{jobId}/resume | POST | 恢复任务 |
| /api/job/{jobId}/execute | POST | 立即执行任务 |

### 日志管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/log/list | GET | 获取日志列表（分页） |
| /api/log/{logId} | GET | 根据ID获取日志详情 |

## 使用示例

### 创建任务

```bash
curl -X POST http://localhost:8080/api/job \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "TestJob",
    "jobGroup": "DEFAULT",
    "jobClass": "com.dw.scheduler.job.DemoJob",
    "cronExpression": "0/10 * * * * ?",
    "status": 0,
    "description": "测试任务",
    "createBy": "admin"
  }'
```

### 启动任务

```bash
curl -X POST http://localhost:8080/api/job/1/start
```

### 查询任务列表

```bash
curl -X GET "http://localhost:8080/api/job/list?pageNum=1&pageSize=10"
```

## 自定义任务

要创建自定义任务，需要继承 `BaseJob` 类并实现 `executeInternal` 方法：

```java
package com.dw.scheduler.job;

import com.dw.scheduler.entity.SchedulerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyCustomJob extends BaseJob {

    @Override
    protected void executeInternal(JobExecutionContext context, SchedulerJob job) throws Exception {
        log.info("My custom job is executing...");

        // 实现你的业务逻辑
        // ...

        log.info("My custom job completed.");
    }
}
```

然后在创建任务时，将 `jobClass` 设置为 `com.dw.scheduler.job.MyCustomJob`。

## Cron表达式示例

| 表达式 | 说明 |
|--------|------|
| `0/30 * * * * ?` | 每30秒执行一次 |
| `0 0/5 * * * ?` | 每5分钟执行一次 |
| `0 0 2 * * ?` | 每天凌晨2点执行 |
| `0 0 8 * * ?` | 每天上午8点执行 |
| `0 0 0 1 * ?` | 每月1号凌晨执行 |
| `0 0 9 ? * MON-FRI` | 工作日上午9点执行 |

## 数据库表说明

### scheduler_job (任务表)

| 字段 | 类型 | 说明 |
|------|------|------|
| job_id | bigint | 任务ID（主键） |
| job_name | varchar(100) | 任务名称 |
| job_group | varchar(100) | 任务分组 |
| job_class | varchar(255) | 任务执行类 |
| cron_expression | varchar(100) | Cron表达式 |
| status | tinyint(1) | 状态（0-暂停，1-运行） |
| description | varchar(500) | 任务描述 |
| job_data | text | 任务数据（JSON格式） |

### scheduler_job_log (日志表)

| 字段 | 类型 | 说明 |
|------|------|------|
| log_id | bigint | 日志ID（主键） |
| job_id | bigint | 任务ID |
| job_name | varchar(100) | 任务名称 |
| job_group | varchar(100) | 任务分组 |
| status | tinyint(1) | 执行状态（0-失败，1-成功） |
| start_time | datetime | 开始时间 |
| end_time | datetime | 结束时间 |
| execute_time | bigint | 执行时长（毫秒） |
| message | text | 执行信息 |
| exception_info | text | 异常信息 |

## 注意事项

1. **Cron表达式验证**: 系统会自动验证Cron表达式的合法性
2. **任务唯一性**: 同一分组下的任务名称必须唯一
3. **任务状态**: 任务创建后默认为暂停状态，需要手动启动
4. **日志清理**: 建议定期清理历史日志，避免数据量过大
5. **集群部署**: 支持集群部署，Quartz会自动进行任务分配

## 版本历史

- **v1.0.0** (2024-01-20)
  - 初始版本发布
  - 实现基础的任务调度功能
  - 支持任务管理和日志记录

## 开源协议

本项目采用 MIT 协议开源，详见 LICENSE 文件。

## 联系方式

- **项目地址**: https://github.com/yourusername/dw-scheduler-management-system
- **问题反馈**: https://github.com/yourusername/dw-scheduler-management-system/issues
- **邮箱**: contact@example.com

## 致谢

感谢以下开源项目：

- Spring Boot
- Quartz Scheduler
- MyBatis-Plus
- Knife4j
- Druid
