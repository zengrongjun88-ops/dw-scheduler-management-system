# 前端Mock版本实现计划

## 文档版本信息

| 版本号 | 修订日期 | 修订人 | 修订内容 |
|--------|----------|--------|----------|
| V1.0   | 2026-01-22 | Claude | 初始版本 |

---

## 1. 项目概述

### 1.1 背景与目标

**背景**：
- 前端项目已完成基础框架和API对接
- 后端33个API接口已实现但需要数据库和完整环境
- 需要一个独立的Mock版本用于：
  - 前端功能演示和验证
  - UI/UX测试和优化
  - 产品原型展示
  - 用户培训和文档编写

**目标**：
- 创建完全独立的Mock前端版本
- 不依赖后端服务，所有数据Mock
- 保持与真实版本一致的交互体验
- 功能完整可用，可直接演示

### 1.2 实现方案

采用**Mock Service Worker (MSW)** + **本地Mock数据**的方案：

```
前端页面 → Mock Service Worker → Mock数据
         (拦截API请求)      (返回模拟数据)
```

**优势**：
- ✅ 不修改现有代码
- ✅ 通过Service Worker拦截API请求
- ✅ 开发/生产环境无缝切换
- ✅ 真实的网络请求体验
- ✅ 支持增删改查的完整模拟

---

## 2. Mock数据设计

### 2.1 核心数据结构

#### 2.1.1 任务目录数据

```typescript
const mockDirectories = [
  {
    id: 1,
    directoryName: '数据仓库',
    parentId: 0,
    directoryPath: '/数据仓库',
    owner: 'admin',
    description: '数据仓库相关任务',
    createTime: '2026-01-01 10:00:00',
    updateTime: '2026-01-20 15:30:00',
    deleted: 0
  },
  {
    id: 2,
    directoryName: 'ODS层',
    parentId: 1,
    directoryPath: '/数据仓库/ODS层',
    owner: 'admin',
    description: 'ODS层数据处理',
    createTime: '2026-01-01 10:05:00',
    updateTime: '2026-01-20 15:30:00',
    deleted: 0
  },
  {
    id: 3,
    directoryName: 'DWD层',
    parentId: 1,
    directoryPath: '/数据仓库/DWD层',
    owner: 'zhangsan',
    description: 'DWD层数据处理',
    createTime: '2026-01-01 10:10:00',
    updateTime: '2026-01-20 15:30:00',
    deleted: 0
  },
  {
    id: 4,
    directoryName: '实时计算',
    parentId: 0,
    directoryPath: '/实时计算',
    owner: 'lisi',
    description: '实时计算任务',
    createTime: '2026-01-01 10:15:00',
    updateTime: '2026-01-20 15:30:00',
    deleted: 0
  },
  {
    id: 5,
    directoryName: '报表生成',
    parentId: 0,
    directoryPath: '/报表生成',
    owner: 'wangwu',
    description: '报表生成任务',
    createTime: '2026-01-01 10:20:00',
    updateTime: '2026-01-20 15:30:00',
    deleted: 0
  }
];
```

#### 2.1.2 任务数据（30+条）

```typescript
const mockTasks = [
  // SQL任务示例
  {
    id: 1,
    taskName: 'user_behavior_etl',
    taskType: 'SQL',
    taskCode: `-- 用户行为ETL任务
INSERT OVERWRITE TABLE dwd.user_behavior
SELECT 
  user_id,
  event_type,
  product_id,
  event_time,
  DATE_FORMAT(event_time, 'yyyy-MM-dd') as dt
FROM ods.user_behavior_log
WHERE dt = '\${yyyyMMdd-1}'
  AND event_type IN ('view', 'click', 'purchase')
GROUP BY user_id, event_type, product_id, event_time;`,
    description: '用户行为数据ETL处理',
    directoryId: 2,
    owner: 'zhangsan@company.com',
    cronExpr: '0 0 2 * * ?',
    timeout: 120,
    status: 'ENABLED',
    priority: 'HIGH',
    datasourceId: 1,
    createTime: '2026-01-10 09:00:00',
    updateTime: '2026-01-21 14:20:00'
  },
  // Shell任务示例
  {
    id: 2,
    taskName: 'data_backup_script',
    taskType: 'SHELL',
    taskCode: `#!/bin/bash
# 数据备份脚本
BACKUP_DIR="/data/backup"
DATE=$(date +%Y%m%d)

echo "开始备份数据..."
hdfs dfs -get /warehouse/dwd/user_behavior $BACKUP_DIR/user_behavior_$DATE

if [ $? -eq 0 ]; then
  echo "备份成功"
  tar -czf $BACKUP_DIR/user_behavior_$DATE.tar.gz $BACKUP_DIR/user_behavior_$DATE
  rm -rf $BACKUP_DIR/user_behavior_$DATE
else
  echo "备份失败"
  exit 1
fi`,
    description: '数据备份脚本',
    directoryId: 2,
    owner: 'lisi@company.com',
    cronExpr: '0 0 3 * * ?',
    timeout: 60,
    status: 'ENABLED',
    priority: 'MEDIUM',
    datasourceId: null,
    createTime: '2026-01-10 09:30:00',
    updateTime: '2026-01-21 14:20:00'
  },
  // Python任务示例
  {
    id: 3,
    taskName: 'ml_model_training',
    taskType: 'PYTHON',
    taskCode: `#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
机器学习模型训练任务
"""
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import joblib

def train_model():
    print("Loading data...")
    df = pd.read_csv('/data/training_data.csv')
    
    X = df.drop('label', axis=1)
    y = df['label']
    
    print("Training model...")
    model = RandomForestClassifier(n_estimators=100)
    model.fit(X, y)
    
    print("Saving model...")
    joblib.dump(model, '/models/user_churn_model.pkl')
    print("Training completed!")

if __name__ == '__main__':
    train_model()`,
    description: '用户流失预测模型训练',
    directoryId: 4,
    owner: 'wangwu@company.com',
    cronExpr: '0 0 1 * * ?',
    timeout: 180,
    status: 'DEVELOPING',
    priority: 'HIGH',
    datasourceId: null,
    createTime: '2026-01-15 10:00:00',
    updateTime: '2026-01-21 16:30:00'
  }
  // ... 继续添加27个任务，覆盖各种场景
];
```

#### 2.1.3 任务依赖数据

```typescript
const mockTaskDependencies = [
  {
    id: 1,
    taskId: 2,
    dependTaskId: 1,
    dependType: 'STRONG',
    cycleOffset: 0,
    createTime: '2026-01-10 10:00:00'
  },
  {
    id: 2,
    taskId: 3,
    dependTaskId: 1,
    dependType: 'STRONG',
    cycleOffset: 0,
    createTime: '2026-01-15 10:30:00'
  },
  // 构建复杂的依赖关系图
  // ...
];
```

#### 2.1.4 数据源数据

```typescript
const mockDatasources = [
  {
    id: 1,
    datasourceName: 'mysql_prod',
    datasourceType: 'MYSQL',
    jdbcUrl: 'jdbc:mysql://192.168.1.100:3306/production',
    username: 'admin',
    password: '******',
    driverClass: 'com.mysql.cj.jdbc.Driver',
    description: '生产环境MySQL数据库',
    createTime: '2026-01-01 08:00:00',
    updateTime: '2026-01-20 10:00:00'
  },
  {
    id: 2,
    datasourceName: 'hive_warehouse',
    datasourceType: 'HIVE',
    jdbcUrl: 'jdbc:hive2://192.168.1.101:10000/warehouse',
    username: 'hive',
    password: '******',
    driverClass: 'org.apache.hive.jdbc.HiveDriver',
    description: 'Hive数仓',
    createTime: '2026-01-01 08:30:00',
    updateTime: '2026-01-20 10:00:00'
  },
  {
    id: 3,
    datasourceName: 'clickhouse_olap',
    datasourceType: 'CLICKHOUSE',
    jdbcUrl: 'jdbc:clickhouse://192.168.1.102:8123/olap',
    username: 'default',
    password: '******',
    driverClass: 'ru.yandex.clickhouse.ClickHouseDriver',
    description: 'ClickHouse OLAP数据库',
    createTime: '2026-01-05 09:00:00',
    updateTime: '2026-01-20 10:00:00'
  }
];
```

#### 2.1.5 任务实例数据（100+条）

```typescript
const mockInstances = [
  {
    id: 1001,
    instanceName: 'user_behavior_etl_20260121_001',
    taskId: 1,
    businessDate: '2026-01-21',
    status: 'SUCCESS',
    triggerType: 'AUTO',
    startTime: '2026-01-21 02:00:05',
    endTime: '2026-01-21 02:15:30',
    executeTime: 925, // 秒
    workerId: 'worker-192.168.1.10',
    retryTimes: 0,
    errorMsg: null,
    createTime: '2026-01-21 02:00:00'
  },
  {
    id: 1002,
    instanceName: 'user_behavior_etl_20260120_001',
    taskId: 1,
    businessDate: '2026-01-20',
    status: 'SUCCESS',
    triggerType: 'AUTO',
    startTime: '2026-01-20 02:00:03',
    endTime: '2026-01-20 02:14:25',
    executeTime: 862,
    workerId: 'worker-192.168.1.10',
    retryTimes: 0,
    errorMsg: null,
    createTime: '2026-01-20 02:00:00'
  },
  {
    id: 1003,
    instanceName: 'data_backup_script_20260121_001',
    taskId: 2,
    businessDate: '2026-01-21',
    status: 'RUNNING',
    triggerType: 'AUTO',
    startTime: '2026-01-21 03:00:05',
    endTime: null,
    executeTime: null,
    workerId: 'worker-192.168.1.11',
    retryTimes: 0,
    errorMsg: null,
    createTime: '2026-01-21 03:00:00'
  },
  {
    id: 1004,
    instanceName: 'ml_model_training_20260121_001',
    taskId: 3,
    businessDate: '2026-01-21',
    status: 'FAILED',
    triggerType: 'AUTO',
    startTime: '2026-01-21 01:00:05',
    endTime: '2026-01-21 01:25:30',
    executeTime: 1525,
    workerId: 'worker-192.168.1.12',
    retryTimes: 2,
    errorMsg: 'FileNotFoundError: /data/training_data.csv not found',
    createTime: '2026-01-21 01:00:00'
  }
  // 生成不同状态、不同日期的实例...
];
```

#### 2.1.6 实例日志数据

```typescript
const mockInstanceLogs = {
  1001: [
    '2026-01-21 02:00:05 [INFO] 任务开始执行',
    '2026-01-21 02:00:06 [INFO] 连接数据库: jdbc:mysql://192.168.1.100:3306/production',
    '2026-01-21 02:00:07 [INFO] 数据库连接成功',
    '2026-01-21 02:00:08 [INFO] 开始执行SQL...',
    '2026-01-21 02:01:30 [INFO] 查询ODS表数据，业务日期: 2026-01-20',
    '2026-01-21 02:02:45 [INFO] 读取到 1,234,567 条记录',
    '2026-01-21 02:05:20 [INFO] 数据清洗完成，有效记录: 1,230,125 条',
    '2026-01-21 02:10:15 [INFO] 开始写入DWD表...',
    '2026-01-21 02:14:50 [INFO] 写入完成，共写入 1,230,125 条记录',
    '2026-01-21 02:15:25 [INFO] 提交事务',
    '2026-01-21 02:15:30 [INFO] 任务执行成功，总耗时: 925秒'
  ],
  1004: [
    '2026-01-21 01:00:05 [INFO] 任务开始执行',
    '2026-01-21 01:00:06 [INFO] Python环境: /usr/bin/python3.8',
    '2026-01-21 01:00:07 [INFO] 加载训练数据...',
    '2026-01-21 01:00:08 [ERROR] FileNotFoundError: /data/training_data.csv not found',
    '2026-01-21 01:00:09 [ERROR] Traceback (most recent call last):',
    '2026-01-21 01:00:09 [ERROR]   File "train.py", line 12, in train_model',
    '2026-01-21 01:00:09 [ERROR]     df = pd.read_csv(\'/data/training_data.csv\')',
    '2026-01-21 01:00:09 [ERROR] FileNotFoundError: [Errno 2] No such file or directory',
    '2026-01-21 01:00:10 [WARN] 准备重试，第1次',
    // 重试逻辑...
    '2026-01-21 01:25:30 [ERROR] 任务执行失败，已达到最大重试次数'
  ]
};
```

#### 2.1.7 服务器数据

```typescript
const mockServers = [
  {
    id: 1,
    serverName: 'worker-prod-01',
    ipAddress: '192.168.1.10',
    serverRole: 'WORKER',
    resourceGroup: 'PRODUCTION',
    status: 'ONLINE',
    cpuCores: 16,
    memorySize: 32768,
    maxTaskNum: 10,
    currentTaskNum: 3,
    lastHeartbeat: '2026-01-22 10:35:00',
    createTime: '2026-01-01 08:00:00',
    updateTime: '2026-01-22 10:35:00'
  },
  {
    id: 2,
    serverName: 'worker-prod-02',
    ipAddress: '192.168.1.11',
    serverRole: 'WORKER',
    resourceGroup: 'PRODUCTION',
    status: 'ONLINE',
    cpuCores: 16,
    memorySize: 32768,
    maxTaskNum: 10,
    currentTaskNum: 5,
    lastHeartbeat: '2026-01-22 10:35:00',
    createTime: '2026-01-01 08:00:00',
    updateTime: '2026-01-22 10:35:00'
  },
  {
    id: 3,
    serverName: 'worker-prod-03',
    ipAddress: '192.168.1.12',
    serverRole: 'WORKER',
    resourceGroup: 'PRODUCTION',
    status: 'OFFLINE',
    cpuCores: 16,
    memorySize: 32768,
    maxTaskNum: 10,
    currentTaskNum: 0,
    lastHeartbeat: '2026-01-22 09:15:00',
    createTime: '2026-01-01 08:00:00',
    updateTime: '2026-01-22 09:15:00'
  },
  {
    id: 4,
    serverName: 'master-prod-01',
    ipAddress: '192.168.1.5',
    serverRole: 'MASTER',
    resourceGroup: 'PRODUCTION',
    status: 'ONLINE',
    cpuCores: 8,
    memorySize: 16384,
    maxTaskNum: 0,
    currentTaskNum: 0,
    lastHeartbeat: '2026-01-22 10:35:00',
    createTime: '2026-01-01 08:00:00',
    updateTime: '2026-01-22 10:35:00'
  }
];
```

#### 2.1.8 服务器监控数据

```typescript
const mockServerMonitor = {
  1: {
    cpu: generateTimeSeriesData(60, 30, 80), // 生成60个数据点
    memory: generateTimeSeriesData(60, 40, 70),
    disk: generateTimeSeriesData(60, 50, 60),
    network: generateTimeSeriesData(60, 100, 500)
  }
  // 其他服务器...
};
```

---

## 3. Mock服务实现方案

### 3.1 技术选型

使用 **Mock Service Worker (MSW)** 方案：

**优势**：
1. 不修改现有代码
2. 拦截网络请求层面
3. 开发体验接近真实API
4. 支持完整的CRUD模拟
5. 易于切换真实/Mock环境

**安装**：
```bash
npm install msw --save-dev
```

### 3.2 目录结构

```
src/
├── mocks/
│   ├── data/                  # Mock数据
│   │   ├── directories.ts
│   │   ├── tasks.ts
│   │   ├── datasources.ts
│   │   ├── instances.ts
│   │   ├── servers.ts
│   │   └── index.ts
│   ├── handlers/              # MSW处理器
│   │   ├── directory.ts
│   │   ├── task.ts
│   │   ├── datasource.ts
│   │   ├── instance.ts
│   │   ├── server.ts
│   │   └── index.ts
│   ├── utils/                 # Mock工具函数
│   │   ├── pagination.ts
│   │   ├── filter.ts
│   │   └── generator.ts
│   ├── browser.ts             # 浏览器端MSW配置
│   └── README.md              # Mock使用文档
```

### 3.3 实现示例

#### 3.3.1 任务列表Mock Handler

```typescript
// src/mocks/handlers/task.ts
import { rest } from 'msw';
import { mockTasks } from '../data/tasks';
import { paginate, filterTasks } from '../utils';

export const taskHandlers = [
  // 查询任务列表
  rest.get('/api/v1/tasks', (req, res, ctx) => {
    const pageNum = Number(req.url.searchParams.get('pageNum')) || 1;
    const pageSize = Number(req.url.searchParams.get('pageSize')) || 10;
    const taskName = req.url.searchParams.get('taskName');
    const taskType = req.url.searchParams.get('taskType');
    const status = req.url.searchParams.get('status');
    
    // 过滤
    let filtered = filterTasks(mockTasks, { taskName, taskType, status });
    
    // 分页
    const result = paginate(filtered, pageNum, pageSize);
    
    return res(
      ctx.delay(500), // 模拟网络延迟
      ctx.status(200),
      ctx.json({
        code: 200,
        message: 'success',
        data: result,
        timestamp: Date.now()
      })
    );
  }),
  
  // 查询任务详情
  rest.get('/api/v1/tasks/:id', (req, res, ctx) => {
    const { id } = req.params;
    const task = mockTasks.find(t => t.id === Number(id));
    
    if (!task) {
      return res(
        ctx.status(404),
        ctx.json({
          code: 404,
          message: '任务不存在',
          data: null,
          timestamp: Date.now()
        })
      );
    }
    
    return res(
      ctx.delay(300),
      ctx.status(200),
      ctx.json({
        code: 200,
        message: 'success',
        data: task,
        timestamp: Date.now()
      })
    );
  }),
  
  // 创建任务
  rest.post('/api/v1/tasks', async (req, res, ctx) => {
    const newTask = await req.json();
    
    // 生成ID
    const maxId = Math.max(...mockTasks.map(t => t.id));
    const task = {
      ...newTask,
      id: maxId + 1,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    };
    
    // 添加到mock数据
    mockTasks.push(task);
    
    return res(
      ctx.delay(500),
      ctx.status(200),
      ctx.json({
        code: 200,
        message: '创建成功',
        data: task,
        timestamp: Date.now()
      })
    );
  }),
  
  // 更新任务
  rest.put('/api/v1/tasks/:id', async (req, res, ctx) => {
    const { id } = req.params;
    const updates = await req.json();
    const index = mockTasks.findIndex(t => t.id === Number(id));
    
    if (index === -1) {
      return res(ctx.status(404), ctx.json({ code: 404, message: '任务不存在' }));
    }
    
    mockTasks[index] = {
      ...mockTasks[index],
      ...updates,
      updateTime: new Date().toISOString()
    };
    
    return res(
      ctx.delay(500),
      ctx.status(200),
      ctx.json({
        code: 200,
        message: '更新成功',
        data: mockTasks[index],
        timestamp: Date.now()
      })
    );
  }),
  
  // 删除任务
  rest.delete('/api/v1/tasks/:id', (req, res, ctx) => {
    const { id } = req.params;
    const index = mockTasks.findIndex(t => t.id === Number(id));
    
    if (index === -1) {
      return res(ctx.status(404), ctx.json({ code: 404, message: '任务不存在' }));
    }
    
    mockTasks.splice(index, 1);
    
    return res(
      ctx.delay(500),
      ctx.status(200),
      ctx.json({
        code: 200,
        message: '删除成功',
        data: null,
        timestamp: Date.now()
      })
    );
  }),
  
  // 查询任务DAG图
  rest.get('/api/v1/tasks/:id/dag', (req, res, ctx) => {
    const { id } = req.params;
    // 构建DAG数据
    const dag = buildTaskDag(Number(id));
    
    return res(
      ctx.delay(800),
      ctx.status(200),
      ctx.json({
        code: 200,
        message: 'success',
        data: dag,
        timestamp: Date.now()
      })
    );
  })
];
```

### 3.4 启用Mock

```typescript
// src/mocks/browser.ts
import { setupWorker } from 'msw';
import { handlers } from './handlers';

export const worker = setupWorker(...handlers);

// src/main.tsx
import { worker } from './mocks/browser';

// 启动Mock Service Worker（仅在开发环境）
if (import.meta.env.DEV && import.meta.env.VITE_ENABLE_MOCK === 'true') {
  worker.start({
    onUnhandledRequest: 'bypass' // 未匹配的请求直接通过
  });
}
```

### 3.5 环境配置

```env
# .env.mock
VITE_ENABLE_MOCK=true
VITE_API_BASE_URL=/api
VITE_APP_TITLE=调度管理系统 (Mock演示版)
```

---

## 4. Mock功能清单

### 4.1 任务管理模块

| 功能 | Mock状态 | 说明 |
|------|---------|------|
| 任务列表查询 | ✅ | 支持分页、搜索、排序 |
| 任务创建 | ✅ | 模拟创建，数据保存在内存 |
| 任务编辑 | ✅ | 支持修改所有字段 |
| 任务删除 | ✅ | 从内存中删除 |
| 任务详情 | ✅ | 完整的任务信息 |
| 依赖管理 | ✅ | 添加/删除依赖，循环检测 |
| DAG图展示 | ✅ | 动态构建依赖关系图 |
| 状态切换 | ✅ | 启用/禁用/开发中 |

### 4.2 数据源管理模块

| 功能 | Mock状态 | 说明 |
|------|---------|------|
| 数据源列表 | ✅ | 支持分页 |
| 数据源CRUD | ✅ | 完整的增删改查 |
| 连接测试 | ✅ | 模拟连接测试（随机成功/失败） |

### 4.3 实例管理模块

| 功能 | Mock状态 | 说明 |
|------|---------|------|
| 实例列表 | ✅ | 支持多条件搜索、筛选 |
| 实例详情 | ✅ | 完整的实例信息 |
| 日志查看 | ✅ | 模拟实时日志滚动 |
| 实例DAG | ✅ | 实例级依赖关系 |
| 重跑实例 | ✅ | 模拟重跑操作 |
| 取消实例 | ✅ | 模拟取消运行中实例 |
| 状态更新 | ✅ | 模拟实时状态变化 |

### 4.4 服务器管理模块

| 功能 | Mock状态 | 说明 |
|------|---------|------|
| 服务器列表 | ✅ | 支持分页 |
| 服务器详情 | ✅ | 完整信息 |
| 资源监控 | ✅ | 模拟实时监控数据 |
| 运行任务 | ✅ | 当前运行的任务列表 |

### 4.5 目录管理模块

| 功能 | Mock状态 | 说明 |
|------|---------|------|
| 目录树 | ✅ | 完整的树形结构 |
| 目录CRUD | ✅ | 增删改查 |
| 目录搜索 | ✅ | 按名称搜索 |

---

## 5. 特殊功能实现

### 5.1 实时日志模拟

```typescript
// 模拟实时日志推送
function* logGenerator(instanceId: number) {
  const logs = mockInstanceLogs[instanceId] || [];
  let index = 0;
  
  while (index < logs.length) {
    yield logs[index];
    index++;
  }
}

// 在组件中使用
const LogViewer = ({ instanceId }) => {
  const [logs, setLogs] = useState([]);
  
  useEffect(() => {
    const generator = logGenerator(instanceId);
    const interval = setInterval(() => {
      const { value, done } = generator.next();
      if (!done) {
        setLogs(prev => [...prev, value]);
      } else {
        clearInterval(interval);
      }
    }, 500); // 每500ms推送一条日志
    
    return () => clearInterval(interval);
  }, [instanceId]);
  
  return <LogViewerComponent logs={logs} />;
};
```

### 5.2 监控数据模拟

```typescript
// 生成时序数据
function generateTimeSeriesData(points: number, min: number, max: number) {
  const data = [];
  const now = Date.now();
  
  for (let i = 0; i < points; i++) {
    data.push({
      timestamp: now - (points - i) * 60000, // 每分钟一个点
      value: Math.random() * (max - min) + min
    });
  }
  
  return data;
}

// 实时更新监控数据
setInterval(() => {
  updateMonitorData(serverId, {
    cpu: generateNewDataPoint(),
    memory: generateNewDataPoint(),
    // ...
  });
}, 5000); // 每5秒更新一次
```

### 5.3 状态变化模拟

```typescript
// 模拟实例状态变化
const simulateInstanceStateChange = (instanceId: number) => {
  const instance = mockInstances.find(i => i.id === instanceId);
  
  if (instance.status === 'RUNNING') {
    // 30%概率成功，10%概率失败，60%继续运行
    const random = Math.random();
    if (random < 0.3) {
      instance.status = 'SUCCESS';
      instance.endTime = new Date().toISOString();
    } else if (random < 0.4) {
      instance.status = 'FAILED';
      instance.endTime = new Date().toISOString();
      instance.errorMsg = 'Mock error: Execution failed';
    }
  }
};
```

---

## 6. 实施步骤

### Phase 1: 准备工作（0.5天）

1. ✅ 创建Mock分支
2. ✅ 安装MSW依赖
3. ✅ 创建Mock目录结构
4. ✅ 编写Mock使用文档

### Phase 2: Mock数据生成（1天）

1. ✅ 生成任务目录数据（5条）
2. ✅ 生成任务数据（30条，覆盖SQL/Shell/Python）
3. ✅ 生成依赖关系数据（构建复杂DAG）
4. ✅ 生成数据源数据（10条）
5. ✅ 生成实例数据（100条，不同状态）
6. ✅ 生成日志数据
7. ✅ 生成服务器数据（5条）
8. ✅ 生成监控数据

### Phase 3: Mock Handler实现（1.5天）

1. ✅ 实现任务管理Handler（10个接口）
2. ✅ 实现数据源管理Handler（6个接口）
3. ✅ 实现实例管理Handler（6个接口）
4. ✅ 实现服务器管理Handler（6个接口）
5. ✅ 实现目录管理Handler（5个接口）

### Phase 4: 特殊功能实现（1天）

1. ✅ 实现实时日志推送模拟
2. ✅ 实现监控数据实时更新
3. ✅ 实现状态变化模拟
4. ✅ 实现DAG图动态构建
5. ✅ 实现搜索过滤功能

### Phase 5: 功能验证（1天）

1. ✅ 测试所有页面功能
2. ✅ 验证交互逻辑
3. ✅ 验证数据一致性
4. ✅ 修复发现的问题

### Phase 6: 文档与交付（0.5天）

1. ✅ 编写Mock使用文档
2. ✅ 录制演示视频
3. ✅ 提交到GitHub
4. ✅ 发布Release

**总计**: 约 5.5 天

---

## 7. 环境切换

### 7.1 启动Mock版本

```bash
# 使用Mock环境变量启动
cp .env.mock .env.local
npm run dev

# 或直接指定
VITE_ENABLE_MOCK=true npm run dev
```

### 7.2 启动真实版本

```bash
# 使用真实环境变量
cp .env.development .env.local
npm run dev

# 或禁用Mock
VITE_ENABLE_MOCK=false npm run dev
```

### 7.3 构建Mock演示版本

```bash
# 构建包含Mock的版本
VITE_ENABLE_MOCK=true npm run build

# 部署到演示环境
npm run preview
```

---

## 8. Mock数据持久化（可选）

使用 localStorage 保存Mock数据，支持刷新后保持：

```typescript
// src/mocks/utils/storage.ts
export const saveToStorage = (key: string, data: any) => {
  localStorage.setItem(`mock_${key}`, JSON.stringify(data));
};

export const loadFromStorage = (key: string, defaultData: any) => {
  const stored = localStorage.getItem(`mock_${key}`);
  return stored ? JSON.parse(stored) : defaultData;
};

// 初始化时加载
let mockTasks = loadFromStorage('tasks', initialMockTasks);

// 修改后保存
const createTask = (task) => {
  mockTasks.push(task);
  saveToStorage('tasks', mockTasks);
};
```

---

## 9. 测试计划

### 9.1 功能测试清单

**任务管理**:
- [ ] 创建各类型任务（SQL/Shell/Python）
- [ ] 编辑任务代码
- [ ] 添加任务依赖
- [ ] 查看DAG图（放大、缩小、拖拽）
- [ ] 修改任务状态
- [ ] 删除任务
- [ ] 搜索任务
- [ ] 分页功能

**数据源管理**:
- [ ] 创建各类型数据源
- [ ] 测试连接（成功/失败场景）
- [ ] 编辑数据源
- [ ] 删除数据源

**实例管理**:
- [ ] 查看实例列表
- [ ] 搜索筛选实例
- [ ] 查看实例详情
- [ ] 查看实时日志（滚动、高亮）
- [ ] 查看实例DAG图
- [ ] 重跑失败实例
- [ ] 取消运行中实例

**服务器管理**:
- [ ] 查看服务器列表
- [ ] 查看服务器详情
- [ ] 查看资源监控图表
- [ ] 查看运行任务

**目录管理**:
- [ ] 查看目录树
- [ ] 创建目录
- [ ] 编辑目录
- [ ] 删除目录
- [ ] 搜索目录

### 9.2 性能测试

- [ ] 大数据量列表渲染（1000+条）
- [ ] DAG图渲染（100+节点）
- [ ] 实时日志流畅度
- [ ] 页面切换响应速度

### 9.3 兼容性测试

- [ ] Chrome 最新版
- [ ] Firefox 最新版
- [ ] Safari 最新版
- [ ] Edge 最新版

---

## 10. 文档交付

### 10.1 Mock使用文档

- Mock功能介绍
- 启动方式
- 环境配置
- 功能清单
- 已知限制

### 10.2 演示视频（可选）

- 功能演示视频（10-15分钟）
- 涵盖所有核心功能
- 上传到项目Wiki

---

## 11. 已知限制

1. **数据持久化**: Mock数据存储在内存中，刷新页面后重置（可选使用localStorage）
2. **并发控制**: 不支持多用户并发操作模拟
3. **网络异常**: 不模拟网络超时、断网等异常场景
4. **权限管理**: 不包含用户登录和权限控制
5. **数据量限制**: 建议Mock数据不超过1000条，避免性能问题

---

## 12. 后续优化

1. **数据生成器**: 开发工具自动生成大量Mock数据
2. **场景模拟**: 支持切换不同业务场景
3. **异常模拟**: 模拟各种错误场景
4. **性能分析**: 添加性能监控和分析
5. **自动化测试**: 基于Mock数据编写E2E测试

---

**文档结束**

> 本文档版本: V1.0
> 最后更新时间: 2026-01-22
> 文档维护: Claude
