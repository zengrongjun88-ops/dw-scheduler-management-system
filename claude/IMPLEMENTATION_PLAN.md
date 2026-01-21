# 调度管理系统后端实现计划

## 文档版本信息

| 版本号 | 修订日期 | 修订人 | 修订内容 |
|--------|----------|--------|----------|
| V1.0   | 2026-01-20 | Claude | 初始版本 |

---

## 1. 实现范围说明

### 1.1 本次实现范围

根据需求文档和用户要求，本次实现**调度管理系统后端**，包括：

**✅ 包含的模块**:
1. **任务管理模块**
   - 任务目录管理（树形目录结构）
   - 任务基本信息管理（CRUD）
   - 任务配置管理（调度配置、执行配置、告警配置）
   - 任务依赖管理（上下游依赖关系）
   - 数据源管理（数据库连接配置）

2. **任务列表模块**
   - 任务搜索与筛选
   - 任务列表展示
   - 任务状态管理
   - 任务DAG关系查询接口（为前端DAG图提供数据）

3. **实例管理模块**
   - 实例搜索与筛选
   - 实例列表展示
   - 实例详情查看
   - 实例日志管理
   - 实例操作接口（预留，待Master实现）

4. **服务器管理模块**
   - 服务器信息管理
   - 服务器状态查询
   - 服务器资源监控数据接口

**❌ 不包含的模块**（后续实现）:
1. **Master调度核心**
   - 任务实例生成逻辑
   - 实例调度执行逻辑
   - Worker选择与负载均衡
   - 日志收集与聚合

2. **Worker执行引擎**
   - 任务执行器实现
   - 日志上报机制
   - 状态管理与上报

3. **Netty通信层**
   - Master-Worker通信协议
   - 心跳保活机制
   - 消息序列化与传输

### 1.2 技术栈调整

根据约束文档要求，需要对现有技术栈进行调整：

| 组件 | 原有方案 | 调整后方案 | 原因 |
|------|---------|-----------|------|
| ORM框架 | MyBatis-Plus | Spring Data JPA | 约束文档要求使用JPA |
| 数据库设计 | 手动建表 | JPA自动建表 | 简化数据库管理 |
| 项目结构 | mapper包 | repository包 | 符合JPA规范 |

**保持不变的技术栈**:
- Spring Boot 2.7.18
- MySQL 8.0
- Druid连接池
- Knife4j API文档
- Lombok
- Hutool工具类

---

## 2. 数据库设计

### 2.1 核心表结构设计

根据需求文档第5节，设计以下核心表：

#### 2.1.1 任务目录表（t_directory）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| directory_name | varchar(128) | 目录名称 | NOT NULL |
| parent_id | bigint | 父目录ID（0表示根目录） | INDEX |
| directory_path | varchar(512) | 目录路径（如：/root/sub1/sub2） | INDEX |
| owner | varchar(64) | 责任人 | |
| description | varchar(500) | 目录描述 | |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | |
| update_time | datetime | 更新时间 | |
| create_by | varchar(64) | 创建人 | |
| update_by | varchar(64) | 更新人 | |

#### 2.1.2 任务表（t_task）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| task_name | varchar(128) | 任务名称 | UNIQUE, NOT NULL |
| task_code | text | 任务代码（SQL/Shell等） | |
| task_type | varchar(32) | 任务类型（SQL/SHELL/PYTHON） | NOT NULL, INDEX |
| description | varchar(512) | 任务描述 | |
| directory_id | bigint | 所属目录ID | INDEX |
| cron_expr | varchar(128) | Cron表达式 | |
| offset_days | int | 业务日期偏移天数 | DEFAULT 0 |
| timeout | int | 超时时间（分钟） | DEFAULT 60 |
| retry_times | int | 失败重试次数 | DEFAULT 0 |
| retry_interval | int | 重试间隔（分钟） | DEFAULT 5 |
| priority | varchar(16) | 优先级（HIGH/MEDIUM/LOW） | DEFAULT 'MEDIUM' |
| owner | varchar(256) | 责任人（多个用逗号分隔） | NOT NULL, INDEX |
| subject | varchar(64) | 所属主题/业务分类 | INDEX |
| status | varchar(16) | 任务状态（ENABLED/DISABLED/DEVELOPING） | NOT NULL, INDEX, DEFAULT 'DEVELOPING' |
| resource_group | varchar(64) | 资源分组（生产/测试/开发） | DEFAULT 'DEFAULT' |
| max_concurrent | int | 最大并发实例数 | DEFAULT 1 |
| datasource_id | bigint | 数据源ID（SQL任务用） | |
| alert_enabled | tinyint | 是否启用告警 | DEFAULT 1 |
| alert_users | varchar(500) | 告警接收人 | |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | INDEX |
| update_time | datetime | 更新时间 | INDEX |
| create_by | varchar(64) | 创建人 | |
| update_by | varchar(64) | 更新人 | |

#### 2.1.3 任务依赖表（t_task_dependency）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| task_id | bigint | 任务ID | NOT NULL, INDEX |
| depend_task_id | bigint | 依赖的任务ID | NOT NULL, INDEX |
| depend_type | varchar(16) | 依赖类型（STRONG/WEAK/CONDITIONAL） | DEFAULT 'STRONG' |
| cycle_offset | int | 周期偏移（0表示同周期，-1表示依赖昨天） | DEFAULT 0 |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | |
| create_by | varchar(64) | 创建人 | |

复合唯一索引: (task_id, depend_task_id, deleted)

#### 2.1.4 数据源表（t_datasource）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| datasource_name | varchar(128) | 数据源名称 | UNIQUE, NOT NULL |
| datasource_type | varchar(32) | 数据源类型（MYSQL/HIVE/CLICKHOUSE等） | NOT NULL |
| jdbc_url | varchar(512) | JDBC连接地址 | NOT NULL |
| username | varchar(128) | 用户名 | |
| password | varchar(256) | 密码（加密存储） | |
| driver_class | varchar(256) | 驱动类名 | NOT NULL |
| description | varchar(500) | 描述 | |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | |
| update_time | datetime | 更新时间 | |
| create_by | varchar(64) | 创建人 | |
| update_by | varchar(64) | 更新人 | |

#### 2.1.5 任务实例表（t_task_instance）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| instance_name | varchar(256) | 实例名称 | |
| task_id | bigint | 任务ID | NOT NULL, INDEX |
| task_snapshot | text | 任务配置快照（JSON） | |
| business_date | date | 业务日期 | INDEX |
| status | varchar(16) | 实例状态 | NOT NULL, INDEX |
| trigger_type | varchar(16) | 触发方式 | |
| start_time | datetime | 开始时间 | INDEX |
| end_time | datetime | 结束时间 | |
| execute_time | int | 执行耗时（秒） | |
| worker_id | varchar(64) | 执行Worker节点 | INDEX |
| retry_times | int | 已重试次数 | DEFAULT 0 |
| error_msg | text | 错误信息 | |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | INDEX |
| update_time | datetime | 更新时间 | |

复合索引: (task_id, business_date), (status, create_time)

#### 2.1.6 实例日志表（t_instance_log）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| instance_id | bigint | 实例ID | NOT NULL, INDEX |
| log_content | text | 日志内容 | |
| log_level | varchar(16) | 日志级别（INFO/WARN/ERROR） | |
| log_time | datetime | 日志时间 | |
| create_time | datetime | 创建时间 | |

复合索引: (instance_id, log_time)

#### 2.1.7 服务器表（t_server）

| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | bigint | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| server_name | varchar(128) | 服务器名称 | |
| ip_address | varchar(64) | IP地址 | UNIQUE, NOT NULL |
| server_role | varchar(16) | 服务器角色（MASTER/WORKER） | NOT NULL, INDEX |
| resource_group | varchar(64) | 资源分组 | INDEX |
| status | varchar(16) | 服务器状态（ONLINE/OFFLINE/FAULT） | INDEX |
| cpu_cores | int | CPU核数 | |
| memory_size | bigint | 内存大小（MB） | |
| disk_size | bigint | 磁盘大小（GB） | |
| max_task_num | int | 最大任务数 | DEFAULT 100 |
| current_task_num | int | 当前任务数 | DEFAULT 0 |
| cpu_usage | decimal(5,2) | CPU使用率（%） | |
| memory_usage | decimal(5,2) | 内存使用率（%） | |
| disk_usage | decimal(5,2) | 磁盘使用率（%） | |
| last_heartbeat | datetime | 最后心跳时间 | INDEX |
| deleted | tinyint | 逻辑删除标记 | DEFAULT 0 |
| create_time | datetime | 创建时间 | |
| update_time | datetime | 更新时间 | |

### 2.2 枚举类型定义

#### 任务类型（TaskType）
- SQL
- SHELL
- PYTHON
- SPARK

#### 任务状态（TaskStatus）
- DEVELOPING（开发中）
- ENABLED（已启用）
- DISABLED（已禁用）

#### 依赖类型（DependencyType）
- STRONG（强依赖）
- WEAK（弱依赖）
- CONDITIONAL（条件依赖）

#### 实例状态（InstanceStatus）
- WAITING（等待中）
- RUNNING（运行中）
- SUCCESS（成功）
- FAILED（失败）
- CANCELED（已取消）

#### 触发方式（TriggerType）
- SCHEDULE（自动调度）
- MANUAL（手动触发）
- API（API触发）
- RERUN（重跑）

#### 优先级（Priority）
- HIGH（高）
- MEDIUM（中）
- LOW（低）

---

## 3. 项目架构重构

### 3.1 包结构调整

```
com.dw.scheduler
├── controller          # REST API控制器
│   ├── TaskController
│   ├── DirectoryController
│   ├── DatasourceController
│   ├── TaskInstanceController
│   └── ServerController
├── service            # 业务逻辑层
│   ├── TaskService
│   ├── DirectoryService
│   ├── DatasourceService
│   ├── TaskInstanceService
│   └── ServerService
├── repository         # JPA数据访问层
│   ├── TaskRepository
│   ├── DirectoryRepository
│   ├── DatasourceRepository
│   ├── TaskInstanceRepository
│   └── ServerRepository
├── entity            # JPA实体类
│   ├── Task
│   ├── Directory
│   ├── Datasource
│   ├── TaskDependency
│   ├── TaskInstance
│   ├── InstanceLog
│   └── Server
├── dto               # 数据传输对象
│   ├── request       # 请求DTO
│   └── response      # 响应DTO
├── enums             # 枚举类
│   ├── TaskType
│   ├── TaskStatus
│   ├── InstanceStatus
│   ├── DependencyType
│   ├── TriggerType
│   └── Priority
├── common            # 公共类
│   ├── response      # 统一响应
│   │   ├── Result
│   │   └── ResultCode
│   └── exception     # 异常类
│       ├── BusinessException
│       └── GlobalExceptionHandler
├── config            # 配置类
│   ├── JpaConfig
│   ├── Knife4jConfig
│   └── WebMvcConfig
└── utils             # 工具类
    ├── CronUtils     # Cron表达式工具
    └── DateUtils     # 日期工具
```

### 3.2 依赖调整

需要移除的依赖：
- mybatis-plus-boot-starter
- quartz（暂时不需要，Master实现时再加）

需要新增的依赖：
- spring-boot-starter-data-jpa
- spring-boot-starter-validation

---

## 4. 功能实现计划

### 4.1 第一阶段：基础框架搭建

**时间**: 1小时
**目标**: 完成项目架构重构，搭建基础框架

**任务清单**:
1. ✅ 调整 pom.xml，移除MyBatis-Plus，引入JPA
2. ✅ 创建基础包结构
3. ✅ 创建所有实体类（Entity）
4. ✅ 创建所有枚举类（Enum）
5. ✅ 创建统一响应类（Result、ResultCode）
6. ✅ 创建全局异常处理类
7. ✅ 配置JPA和数据库连接

### 4.2 第二阶段：任务目录管理

**时间**: 30分钟
**目标**: 实现任务目录的树形结构管理

**任务清单**:
1. ✅ 创建 DirectoryRepository
2. ✅ 创建 DirectoryService（创建、更新、删除、查询目录树）
3. ✅ 创建 DirectoryController（提供REST API）
4. ✅ 实现目录树查询算法

**API接口**:
- GET /api/v1/directories/tree - 获取目录树
- GET /api/v1/directories/{id} - 获取目录详情
- POST /api/v1/directories - 创建目录
- PUT /api/v1/directories/{id} - 更新目录
- DELETE /api/v1/directories/{id} - 删除目录

### 4.3 第三阶段：数据源管理

**时间**: 30分钟
**目标**: 实现数据源配置管理

**任务清单**:
1. ✅ 创建 DatasourceRepository
2. ✅ 创建 DatasourceService（CRUD、连接测试）
3. ✅ 创建 DatasourceController
4. ✅ 实现数据源连接测试功能

**API接口**:
- GET /api/v1/datasources - 获取数据源列表
- GET /api/v1/datasources/{id} - 获取数据源详情
- POST /api/v1/datasources - 创建数据源
- PUT /api/v1/datasources/{id} - 更新数据源
- DELETE /api/v1/datasources/{id} - 删除数据源
- POST /api/v1/datasources/{id}/test - 测试数据源连接

### 4.4 第四阶段：任务管理核心功能

**时间**: 2小时
**目标**: 实现任务的完整生命周期管理

**任务清单**:
1. ✅ 创建 TaskRepository
2. ✅ 创建 TaskDependencyRepository
3. ✅ 创建 TaskService（CRUD、状态管理、依赖管理）
4. ✅ 创建 TaskController
5. ✅ 实现任务搜索功能（多条件组合查询）
6. ✅ 实现依赖关系管理
7. ✅ 实现循环依赖检测算法
8. ✅ 实现DAG查询接口

**API接口**:
- GET /api/v1/tasks - 查询任务列表（支持多条件搜索）
- GET /api/v1/tasks/{id} - 查询任务详情
- POST /api/v1/tasks - 创建任务
- PUT /api/v1/tasks/{id} - 更新任务
- DELETE /api/v1/tasks/{id} - 删除任务
- PUT /api/v1/tasks/{id}/status - 修改任务状态（启用/禁用）
- GET /api/v1/tasks/{id}/dependencies - 查询任务依赖关系
- POST /api/v1/tasks/{id}/dependencies - 添加任务依赖
- DELETE /api/v1/tasks/{id}/dependencies/{depId} - 删除任务依赖
- GET /api/v1/tasks/{id}/dag - 查询任务DAG图数据

### 4.5 第五阶段：任务实例管理

**时间**: 1.5小时
**目标**: 实现任务实例的查询和管理

**任务清单**:
1. ✅ 创建 TaskInstanceRepository
2. ✅ 创建 InstanceLogRepository
3. ✅ 创建 TaskInstanceService（查询、日志管理）
4. ✅ 创建 TaskInstanceController
5. ✅ 实现实例搜索功能
6. ✅ 实现实例日志查询功能

**API接口**:
- GET /api/v1/instances - 查询实例列表（支持多条件搜索）
- GET /api/v1/instances/{id} - 查询实例详情
- GET /api/v1/instances/{id}/logs - 查询实例日志
- POST /api/v1/instances/{id}/rerun - 重跑实例（预留接口）
- POST /api/v1/instances/{id}/cancel - 取消实例（预留接口）
- GET /api/v1/instances/{id}/dag - 查询实例DAG图数据

### 4.6 第六阶段：服务器管理

**时间**: 1小时
**目标**: 实现服务器信息管理

**任务清单**:
1. ✅ 创建 ServerRepository
2. ✅ 创建 ServerService（CRUD、状态管理）
3. ✅ 创建 ServerController
4. ✅ 实现服务器列表查询

**API接口**:
- GET /api/v1/servers - 查询服务器列表
- GET /api/v1/servers/{id} - 查询服务器详情
- POST /api/v1/servers - 注册服务器
- PUT /api/v1/servers/{id} - 更新服务器信息
- DELETE /api/v1/servers/{id} - 删除服务器
- GET /api/v1/servers/{id}/monitor - 查询服务器监控数据

### 4.7 第七阶段：功能测试

**时间**: 2小时
**目标**: 进行完整的功能测试，确保所有API正常工作

**任务清单**:
1. ✅ 使用Postman/curl测试所有API接口
2. ✅ 测试参数校验功能
3. ✅ 测试异常处理功能
4. ✅ 测试数据库事务
5. ✅ 编写测试报告
6. ✅ 修复发现的问题

---

## 5. 测试计划

### 5.1 测试环境准备

1. 启动MySQL数据库
2. 配置application.yml中的数据库连接
3. 启动Spring Boot应用
4. 访问Knife4j文档: http://localhost:8080/api/doc.html

### 5.2 测试用例设计

#### 5.2.1 任务目录管理测试

| 测试项 | 测试步骤 | 预期结果 |
|-------|---------|---------|
| 创建根目录 | POST /api/v1/directories，parent_id=0 | 成功创建，返回目录ID |
| 创建子目录 | POST /api/v1/directories，指定parent_id | 成功创建，目录路径正确 |
| 查询目录树 | GET /api/v1/directories/tree | 返回完整树形结构 |
| 更新目录 | PUT /api/v1/directories/{id} | 目录信息更新成功 |
| 删除目录 | DELETE /api/v1/directories/{id} | 目录删除成功（逻辑删除） |

#### 5.2.2 任务管理测试

| 测试项 | 测试步骤 | 预期结果 |
|-------|---------|---------|
| 创建SQL任务 | POST /api/v1/tasks，完整参数 | 成功创建任务 |
| 查询任务列表 | GET /api/v1/tasks，带查询条件 | 返回符合条件的任务列表 |
| 更新任务 | PUT /api/v1/tasks/{id} | 任务信息更新成功 |
| 添加任务依赖 | POST /api/v1/tasks/{id}/dependencies | 依赖关系添加成功 |
| 循环依赖检测 | 添加形成循环的依赖 | 系统拒绝，返回错误提示 |
| 查询DAG图 | GET /api/v1/tasks/{id}/dag | 返回DAG图数据结构 |
| 启用/禁用任务 | PUT /api/v1/tasks/{id}/status | 任务状态切换成功 |
| 删除任务 | DELETE /api/v1/tasks/{id} | 任务删除成功 |

#### 5.2.3 数据源管理测试

| 测试项 | 测试步骤 | 预期结果 |
|-------|---------|---------|
| 创建MySQL数据源 | POST /api/v1/datasources | 数据源创建成功 |
| 测试数据源连接 | POST /api/v1/datasources/{id}/test | 连接测试通过 |
| 查询数据源列表 | GET /api/v1/datasources | 返回数据源列表 |
| 更新数据源 | PUT /api/v1/datasources/{id} | 数据源更新成功 |
| 删除数据源 | DELETE /api/v1/datasources/{id} | 数据源删除成功 |

#### 5.2.4 实例管理测试

| 测试项 | 测试步骤 | 预期结果 |
|-------|---------|---------|
| 查询实例列表 | GET /api/v1/instances | 返回实例列表 |
| 查询实例详情 | GET /api/v1/instances/{id} | 返回实例完整信息 |
| 查询实例日志 | GET /api/v1/instances/{id}/logs | 返回实例日志列表 |
| 多条件搜索 | GET /api/v1/instances，多个查询参数 | 返回符合条件的实例 |

#### 5.2.5 服务器管理测试

| 测试项 | 测试步骤 | 预期结果 |
|-------|---------|---------|
| 注册Worker服务器 | POST /api/v1/servers | 服务器注册成功 |
| 查询服务器列表 | GET /api/v1/servers | 返回服务器列表 |
| 更新服务器信息 | PUT /api/v1/servers/{id} | 服务器信息更新 |
| 查询服务器详情 | GET /api/v1/servers/{id} | 返回服务器详细信息 |

### 5.3 异常场景测试

1. **参数校验测试**
   - 缺少必填参数
   - 参数格式错误
   - 参数长度超限

2. **业务逻辑测试**
   - 重复创建（任务名称唯一性）
   - 循环依赖检测
   - 删除被依赖的任务
   - 删除包含任务的目录

3. **并发测试**
   - 并发创建任务
   - 并发更新同一任务

---

## 6. 文档输出计划

### 6.1 必须输出的文档

1. **架构设计文档**（claude/Architecture.md）
   - 系统整体架构
   - 技术栈说明
   - 数据库设计
   - API设计

2. **项目结构文档**（claude/PROJECT_STRUCTURE.md）
   - 完整的项目目录结构
   - 各模块功能说明
   - 文件定位指南

3. **实现指南文档**（claude/REALTIME_IMPLEMENTATION_GUIDE.md）
   - 已实现功能清单
   - 待实现功能清单
   - 关键技术点说明

4. **验证指南文档**（claude/BROWSER_VERIFICATION_GUIDE.md）
   - 功能验证步骤
   - 测试数据准备
   - 常见问题处理

5. **测试报告**（claude/TEST_REPORT.md）
   - 测试环境说明
   - 测试用例执行结果
   - 问题汇总与解决

---

## 7. 风险与对策

### 7.1 技术风险

| 风险项 | 影响 | 对策 |
|-------|------|------|
| JPA性能问题 | 大数据量查询可能慢 | 合理使用索引，必要时使用原生SQL |
| 循环依赖检测复杂度高 | 可能影响性能 | 使用拓扑排序算法，加缓存 |
| DAG图查询性能 | 多层依赖查询慢 | 限制查询深度，使用递归CTE |

### 7.2 进度风险

| 风险项 | 对策 |
|-------|------|
| 功能开发延期 | 优先完成核心功能，次要功能可后续迭代 |
| 测试时间不足 | 自动化测试，使用Postman集合批量测试 |

---

## 8. 总结

本次实现聚焦于**调度管理系统的管理后端**，为前端提供完整的数据管理能力和API接口。不包含Master调度核心和Worker执行引擎，这些将在后续阶段实现。

**预计总耗时**: 8-10小时

**交付物**:
1. 完整的管理后端代码
2. 数据库表结构（JPA自动生成）
3. API文档（Knife4j自动生成）
4. 完善的技术文档
5. 测试报告

**下一阶段工作**:
1. Master调度核心实现
2. Worker执行引擎实现
3. Netty通信层实现
4. 前端开发

---

**文档结束**
