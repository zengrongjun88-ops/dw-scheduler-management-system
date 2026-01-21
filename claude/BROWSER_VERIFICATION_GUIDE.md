# 浏览器验证指南

## 快速验证步骤

### 1. 启动应用

```bash
cd /Users/zengrongjun/claudespace/dw-scheduler-management-system
mvn spring-boot:run
```

### 2. 访问API文档

打开浏览器访问: http://localhost:8080/api/doc.html

### 3. 测试API接口

#### 3.1 创建任务目录

**接口**: POST /api/v1/directories

**请求体**:
```json
{
  "directoryName": "测试目录",
  "parentId": 0,
  "owner": "admin",
  "description": "这是一个测试目录"
}
```

#### 3.2 创建数据源

**接口**: POST /api/v1/datasources

**请求体**:
```json
{
  "datasourceName": "test_mysql",
  "datasourceType": "MYSQL",
  "jdbcUrl": "jdbc:mysql://localhost:3306/test",
  "username": "root",
  "password": "root",
  "driverClass": "com.mysql.cj.jdbc.Driver"
}
```

#### 3.3 测试数据源连接

**接口**: POST /api/v1/datasources/{id}/test

#### 3.4 创建SQL任务

**接口**: POST /api/v1/tasks

**请求体**:
```json
{
  "taskName": "test_sql_task",
  "taskType": "SQL",
  "taskCode": "SELECT * FROM test_table",
  "directoryId": 1,
  "owner": "admin",
  "cronExpr": "0 0 2 * * ?",
  "timeout": 60,
  "status": "DEVELOPING",
  "datasourceId": 1
}
```

#### 3.5 查询任务列表

**接口**: GET /api/v1/tasks?pageNum=1&pageSize=10

#### 3.6 添加任务依赖

**接口**: POST /api/v1/tasks/{id}/dependencies

**请求体**:
```json
{
  "dependTaskId": 2,
  "dependType": "STRONG",
  "cycleOffset": 0
}
```

#### 3.7 查询任务DAG图

**接口**: GET /api/v1/tasks/{id}/dag

### 4. 验证数据库

连接MySQL查看自动创建的表：

```sql
USE scheduler_db;
SHOW TABLES;
```

应该看到7张表：
- t_directory
- t_task
- t_task_dependency
- t_datasource
- t_task_instance
- t_instance_log
- t_server

### 5. 访问Druid监控

打开浏览器访问: http://localhost:8080/api/druid/login.html

用户名/密码: admin/admin

可以查看SQL执行统计、慢SQL分析、连接池监控等。

## 常见问题

### Q1: 启动失败，提示数据库连接失败？

A: 检查MySQL是否启动，数据库是否存在，用户名密码是否正确。

### Q2: API返回404？

A: 检查context-path是否配置为/api，完整路径应为 http://localhost:8080/api/v1/...

### Q3: JPA建表失败？

A: 检查MySQL用户是否有CREATE权限，检查hibernate.ddl-auto配置。

---
