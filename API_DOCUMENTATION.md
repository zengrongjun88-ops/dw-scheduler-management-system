# DW Scheduler Management System - API Documentation

## 概述
本文档描述了调度管理系统的完整REST API接口。所有接口均遵循RESTful设计规范，使用统一的Result包装类返回数据。

## 基础信息
- **Base URL**: `/api/v1`
- **响应格式**: JSON
- **统一响应结构**:
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1705718400000
}
```

---

## 1. 任务目录管理 (Directory Management)

### 1.1 获取目录树
- **URL**: `GET /api/v1/directories/tree`
- **描述**: 获取完整的目录树形结构
- **响应**: 目录树列表

### 1.2 获取目录详情
- **URL**: `GET /api/v1/directories/{id}`
- **参数**: 
  - `id` (path): 目录ID
- **响应**: 目录详细信息

### 1.3 创建目录
- **URL**: `POST /api/v1/directories`
- **请求体**: Directory对象
- **响应**: 创建后的目录信息

### 1.4 更新目录
- **URL**: `PUT /api/v1/directories/{id}`
- **参数**: 
  - `id` (path): 目录ID
- **请求体**: Directory对象
- **响应**: 更新后的目录信息

### 1.5 删除目录
- **URL**: `DELETE /api/v1/directories/{id}`
- **参数**: 
  - `id` (path): 目录ID
- **响应**: 删除结果

---

## 2. 任务管理 (Task Management)

### 2.1 查询任务列表
- **URL**: `GET /api/v1/tasks`
- **参数**:
  - `taskName` (query, optional): 任务名称（模糊查询）
  - `taskType` (query, optional): 任务类型
  - `status` (query, optional): 任务状态
  - `directoryId` (query, optional): 目录ID
  - `owner` (query, optional): 负责人
  - `subject` (query, optional): 主题
  - `page` (query): 页码（默认0）
  - `size` (query): 每页大小（默认20）
- **响应**: 分页任务列表

### 2.2 查询任务详情
- **URL**: `GET /api/v1/tasks/{id}`
- **参数**: 
  - `id` (path): 任务ID
- **响应**: 任务详细信息

### 2.3 创建任务
- **URL**: `POST /api/v1/tasks`
- **请求体**: Task对象
- **响应**: 创建后的任务信息

### 2.4 更新任务
- **URL**: `PUT /api/v1/tasks/{id}`
- **参数**: 
  - `id` (path): 任务ID
- **请求体**: Task对象
- **响应**: 更新后的任务信息

### 2.5 删除任务
- **URL**: `DELETE /api/v1/tasks/{id}`
- **参数**: 
  - `id` (path): 任务ID
- **响应**: 删除结果

### 2.6 修改任务状态
- **URL**: `PUT /api/v1/tasks/{id}/status`
- **参数**: 
  - `id` (path): 任务ID
  - `status` (query): 目标状态（DEVELOPING/TESTING/ONLINE/OFFLINE）
- **响应**: 更新后的任务信息

### 2.7 查询任务依赖
- **URL**: `GET /api/v1/tasks/{id}/dependencies`
- **参数**: 
  - `id` (path): 任务ID
- **响应**: 依赖关系列表

### 2.8 添加任务依赖
- **URL**: `POST /api/v1/tasks/{id}/dependencies`
- **参数**: 
  - `id` (path): 任务ID
- **请求体**: TaskDependency对象
- **响应**: 创建后的依赖关系

### 2.9 删除任务依赖
- **URL**: `DELETE /api/v1/tasks/{id}/dependencies/{depId}`
- **参数**: 
  - `id` (path): 任务ID
  - `depId` (path): 依赖关系ID
- **响应**: 删除结果

### 2.10 查询任务DAG图
- **URL**: `GET /api/v1/tasks/{id}/dag`
- **参数**: 
  - `id` (path): 任务ID
- **响应**: DAG图数据（节点和边）

---

## 3. 数据源管理 (Datasource Management)

### 3.1 查询数据源列表
- **URL**: `GET /api/v1/datasources`
- **参数**:
  - `datasourceType` (query, optional): 数据源类型
- **响应**: 数据源列表

### 3.2 查询数据源详情
- **URL**: `GET /api/v1/datasources/{id}`
- **参数**: 
  - `id` (path): 数据源ID
- **响应**: 数据源详细信息

### 3.3 创建数据源
- **URL**: `POST /api/v1/datasources`
- **请求体**: Datasource对象
- **响应**: 创建后的数据源信息

### 3.4 更新数据源
- **URL**: `PUT /api/v1/datasources/{id}`
- **参数**: 
  - `id` (path): 数据源ID
- **请求体**: Datasource对象
- **响应**: 更新后的数据源信息

### 3.5 删除数据源
- **URL**: `DELETE /api/v1/datasources/{id}`
- **参数**: 
  - `id` (path): 数据源ID
- **响应**: 删除结果

### 3.6 测试数据源连接
- **URL**: `POST /api/v1/datasources/{id}/test`
- **参数**: 
  - `id` (path): 数据源ID
- **响应**: 连接测试结果

---

## 4. 任务实例管理 (Task Instance Management)

### 4.1 查询实例列表
- **URL**: `GET /api/v1/instances`
- **参数**:
  - `taskId` (query, optional): 任务ID
  - `instanceName` (query, optional): 实例名称（模糊查询）
  - `status` (query, optional): 实例状态
  - `businessDateStart` (query, optional): 业务日期起始
  - `businessDateEnd` (query, optional): 业务日期结束
  - `workerId` (query, optional): 执行服务器ID
  - `page` (query): 页码（默认0）
  - `size` (query): 每页大小（默认20）
- **响应**: 分页实例列表

### 4.2 查询实例详情
- **URL**: `GET /api/v1/instances/{id}`
- **参数**: 
  - `id` (path): 实例ID
- **响应**: 实例详细信息

### 4.3 查询实例日志
- **URL**: `GET /api/v1/instances/{id}/logs`
- **参数**: 
  - `id` (path): 实例ID
  - `level` (query, optional): 日志级别
  - `keyword` (query, optional): 关键词搜索
  - `limit` (query): 返回条数限制（默认1000）
- **响应**: 实例日志列表

### 4.4 查询实例DAG图
- **URL**: `GET /api/v1/instances/{id}/dag`
- **参数**: 
  - `id` (path): 实例ID
- **响应**: 实例DAG图数据

### 4.5 重跑实例（预留）
- **URL**: `POST /api/v1/instances/{id}/rerun`
- **参数**: 
  - `id` (path): 实例ID
  - `rerunDownstream` (query): 是否重跑下游（默认false）
- **响应**: 重跑后的实例信息

### 4.6 取消实例（预留）
- **URL**: `POST /api/v1/instances/{id}/cancel`
- **参数**: 
  - `id` (path): 实例ID
- **响应**: 取消后的实例信息

---

## 5. 服务器管理 (Server Management)

### 5.1 查询服务器列表
- **URL**: `GET /api/v1/servers`
- **参数**:
  - `serverRole` (query, optional): 服务器角色
  - `resourceGroup` (query, optional): 资源组
  - `status` (query, optional): 服务器状态
- **响应**: 服务器列表

### 5.2 查询服务器详情
- **URL**: `GET /api/v1/servers/{id}`
- **参数**: 
  - `id` (path): 服务器ID
- **响应**: 服务器详细信息

### 5.3 注册服务器
- **URL**: `POST /api/v1/servers`
- **请求体**: Server对象
- **响应**: 注册后的服务器信息

### 5.4 更新服务器信息
- **URL**: `PUT /api/v1/servers/{id}`
- **参数**: 
  - `id` (path): 服务器ID
- **请求体**: Server对象
- **响应**: 更新后的服务器信息

### 5.5 删除服务器
- **URL**: `DELETE /api/v1/servers/{id}`
- **参数**: 
  - `id` (path): 服务器ID
- **响应**: 删除结果

### 5.6 查询服务器监控数据
- **URL**: `GET /api/v1/servers/{id}/monitor`
- **参数**: 
  - `id` (path): 服务器ID
  - `duration` (query): 时间范围（分钟，默认60）
- **响应**: 服务器监控数据

---

## 附录

### 任务状态枚举 (TaskStatus)
- `DEVELOPING`: 开发中
- `TESTING`: 测试中
- `ONLINE`: 已上线
- `OFFLINE`: 已下线

### 实例状态枚举 (InstanceStatus)
- `WAITING`: 等待执行
- `RUNNING`: 运行中
- `SUCCESS`: 执行成功
- `FAILED`: 执行失败
- `KILLED`: 已取消

### 服务器角色枚举 (ServerRole)
- `MASTER`: 主节点
- `WORKER`: 工作节点
- `API`: API服务节点

### 服务器状态枚举 (ServerStatus)
- `ONLINE`: 在线
- `OFFLINE`: 离线
- `BUSY`: 繁忙

### 依赖类型枚举 (DependencyType)
- `SELF_DEPEND`: 自依赖
- `NORMAL_DEPEND`: 普通依赖
- `CROSS_DEPEND`: 跨周期依赖

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

**文档版本**: 1.0.0  
**最后更新**: 2024-01-21
