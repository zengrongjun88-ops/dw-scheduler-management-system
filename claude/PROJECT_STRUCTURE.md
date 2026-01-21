# 项目结构文档

## 完整目录结构

```
dw-scheduler-management-system/
├── src/main/java/com/dw/scheduler/
│   ├── SchedulerApplication.java   # 启动类
│   ├── controller/                 # Controller层（5个）
│   │   ├── DirectoryController.java
│   │   ├── TaskController.java
│   │   ├── DatasourceController.java
│   │   ├── TaskInstanceController.java
│   │   └── ServerController.java
│   ├── service/                    # Service接口层（5个）
│   │   ├── DirectoryService.java
│   │   ├── TaskService.java
│   │   ├── DatasourceService.java
│   │   ├── TaskInstanceService.java
│   │   ├── ServerService.java
│   │   └── impl/                  # Service实现层（5个）
│   │       ├── DirectoryServiceImpl.java
│   │       ├── TaskServiceImpl.java
│   │       ├── DatasourceServiceImpl.java
│   │       ├── TaskInstanceServiceImpl.java
│   │       └── ServerServiceImpl.java
│   ├── repository/                # Repository层（7个）
│   │   ├── DirectoryRepository.java
│   │   ├── TaskRepository.java
│   │   ├── TaskDependencyRepository.java
│   │   ├── DatasourceRepository.java
│   │   ├── TaskInstanceRepository.java
│   │   ├── InstanceLogRepository.java
│   │   └── ServerRepository.java
│   ├── entity/                    # 实体类（7个）
│   │   ├── Directory.java
│   │   ├── Task.java
│   │   ├── TaskDependency.java
│   │   ├── Datasource.java
│   │   ├── TaskInstance.java
│   │   ├── InstanceLog.java
│   │   └── Server.java
│   ├── enums/                     # 枚举类（9个）
│   │   ├── TaskType.java
│   │   ├── TaskStatus.java
│   │   ├── InstanceStatus.java
│   │   ├── DependencyType.java
│   │   ├── TriggerType.java
│   │   ├── Priority.java
│   │   ├── ServerRole.java
│   │   ├── ServerStatus.java
│   │   └── LogLevel.java
│   ├── common/                    # 公共类
│   │   ├── response/
│   │   │   ├── Result.java
│   │   │   └── ResultCode.java
│   │   └── exception/
│   │       ├── BusinessException.java
│   │       └── GlobalExceptionHandler.java
│   └── config/                    # 配置类（3个）
│       ├── JpaConfig.java
│       ├── Knife4jConfig.java
│       └── WebMvcConfig.java
├── src/main/resources/
│   └── application.yml            # 配置文件
├── claude/                        # 文档目录
│   ├── REQUIREMENT.md            # 需求文档
│   ├── CLAUDE.md                 # 约束文档
│   ├── IMPLEMENTATION_PLAN.md    # 实施计划
│   ├── Architecture.md           # 架构设计
│   ├── PROJECT_STRUCTURE.md      # 项目结构
│   ├── REALTIME_IMPLEMENTATION_GUIDE.md   # 实现指南
│   ├── BROWSER_VERIFICATION_GUIDE.md      # 验证指南
│   └── TEST_REPORT.md                     # 测试报告
├── pom.xml                        # Maven配置
└── README.md                      # 项目说明

```

## 核心文件统计

- **总Java文件数**: 50个
- **Controller**: 5个
- **Service**: 10个（5接口+5实现）
- **Repository**: 7个
- **Entity**: 7个
- **Enum**: 9个
- **Config**: 3个
- **Common**: 4个
- **总代码行数**: 约3000+行

## 模块说明

### 1. Controller层
处理HTTP请求，提供REST API接口。

### 2. Service层
业务逻辑处理，事务管理。

### 3. Repository层
数据访问接口，基于Spring Data JPA。

### 4. Entity层
JPA实体类，映射数据库表。

### 5. Common层
公共类，包含统一响应和异常处理。

### 6. Config层
配置类，包含JPA、Swagger、Web配置。

---
