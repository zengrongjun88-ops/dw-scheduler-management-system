# 项目架构设计文档

## 系统整体架构

本项目采用前后端分离架构，当前实现为管理后端部分。

### 技术栈

- Java 1.8
- Spring Boot 2.7.18
- Spring Data JPA
- MySQL 8.0.33
- Druid 1.2.20
- Knife4j 3.0.3

### 分层架构

Controller → Service → Repository → Entity

### 数据库设计

7张核心表：
1. t_directory - 任务目录
2. t_task - 任务配置
3. t_task_dependency - 任务依赖
4. t_datasource - 数据源
5. t_task_instance - 任务实例
6. t_instance_log - 实例日志
7. t_server - 服务器信息

JPA自动建表（ddl-auto: update）

### 关键技术

- JPA动态查询（Specification）
- 循环依赖检测（DFS算法）
- DAG图构建（递归算法）

### API接口

已实现33个REST API接口，统一使用Result包装返回结果。

---
