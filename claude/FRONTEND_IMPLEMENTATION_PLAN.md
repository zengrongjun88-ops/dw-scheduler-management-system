# 前端实现计划

## 文档版本信息

| 版本号 | 修订日期 | 修订人 | 修订内容 |
|--------|----------|--------|----------|
| V1.0   | 2026-01-21 | Claude | 初始版本 |

---

## 1. 项目概述

### 1.1 目标

基于后端提供的33个API接口，实现调度系统管理前端，提供直观易用的任务管理、实例监控、服务器管理界面。

### 1.2 技术选型

#### 核心技术栈

- **框架**: React 18.2.0 (稳定版本)
- **语言**: TypeScript 5.0+
- **UI组件库**: Ant Design 5.12.0
- **状态管理**: Redux Toolkit + React Query
- **路由**: React Router v6
- **HTTP客户端**: Axios
- **代码编辑器**: Monaco Editor (VS Code内核)
- **图表库**: ECharts 5.4.0
- **DAG可视化**: AntV G6 4.8.0
- **实时通信**: WebSocket (原生)
- **构建工具**: Vite 5.0
- **代码规范**: ESLint + Prettier
- **CSS方案**: CSS Modules + Less

#### 开发工具

- **包管理器**: pnpm
- **版本控制**: Git
- **API文档**: 后端Knife4j文档
- **Mock工具**: Mock Service Worker (开发阶段可选)

### 1.3 目录结构设计

```
frontend/
├── public/                  # 静态资源
│   └── index.html
├── src/
│   ├── api/                # API接口定义
│   │   ├── directory.ts
│   │   ├── task.ts
│   │   ├── datasource.ts
│   │   ├── instance.ts
│   │   ├── server.ts
│   │   └── request.ts      # Axios封装
│   ├── assets/             # 静态资源（图片、字体等）
│   ├── components/         # 通用组件
│   │   ├── CodeEditor/     # 代码编辑器
│   │   ├── DagGraph/       # DAG图组件
│   │   ├── LogViewer/      # 日志查看器
│   │   └── ...
│   ├── pages/              # 页面组件
│   │   ├── Directory/      # 任务目录管理
│   │   ├── Task/           # 任务管理
│   │   ├── Datasource/     # 数据源管理
│   │   ├── Instance/       # 任务实例管理
│   │   ├── Server/         # 服务器管理
│   │   └── Layout/         # 布局组件
│   ├── hooks/              # 自定义Hooks
│   ├── store/              # Redux状态管理
│   ├── types/              # TypeScript类型定义
│   ├── utils/              # 工具函数
│   ├── styles/             # 全局样式
│   ├── App.tsx             # 根组件
│   └── main.tsx            # 入口文件
├── .env.development        # 开发环境配置
├── .env.production         # 生产环境配置
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

---

## 2. 核心功能模块设计

### 2.1 任务目录管理模块

#### 功能点
- ✅ 树形目录展示（支持多级嵌套）
- ✅ 目录CRUD操作（创建、重命名、删除）
- ✅ 拖拽调整目录层级
- ✅ 目录搜索与快速定位

#### 页面组件
- `DirectoryTree.tsx` - 目录树组件
- `DirectoryModal.tsx` - 目录编辑弹窗
- `DirectorySearch.tsx` - 目录搜索组件

#### 技术要点
- 使用 Ant Design `Tree` 组件实现树形结构
- 支持拖拽功能（`draggable` 属性）
- 递归渲染目录节点
- 本地搜索过滤

### 2.2 任务管理模块

#### 功能点
- ✅ 任务列表展示（分页、搜索、排序）
- ✅ 任务创建/编辑（表单验证）
- ✅ 代码编辑器（SQL/Shell语法高亮）
- ✅ 依赖管理（添加/删除依赖）
- ✅ DAG依赖图可视化
- ✅ 任务状态切换（启用/禁用/开发中）
- ✅ 批量操作（批量删除、批量修改状态）

#### 页面组件
- `TaskList.tsx` - 任务列表页
- `TaskForm.tsx` - 任务创建/编辑表单
- `TaskCodeEditor.tsx` - 任务代码编辑器
- `TaskDependency.tsx` - 依赖管理组件
- `TaskDagGraph.tsx` - 任务DAG图

#### 技术要点

**代码编辑器**:
```typescript
// 使用 Monaco Editor
import Editor from '@monaco-editor/react';

<Editor
  height="400px"
  language="sql" // 或 "shell"
  theme="vs-dark"
  value={code}
  onChange={handleCodeChange}
  options={{
    minimap: { enabled: false },
    fontSize: 14,
    lineNumbers: 'on',
    automaticLayout: true
  }}
/>
```

**DAG图可视化**:
```typescript
// 使用 AntV G6
import { Graph } from '@antv/g6';

const graph = new Graph({
  container: 'dag-container',
  width: 800,
  height: 600,
  layout: {
    type: 'dagre',
    rankdir: 'TB', // 自顶向下
    nodesep: 30,
    ranksep: 50
  },
  modes: {
    default: ['drag-canvas', 'zoom-canvas', 'drag-node']
  }
});

// 渲染数据
graph.data({
  nodes: [...],
  edges: [...]
});
graph.render();
```

### 2.3 数据源管理模块

#### 功能点
- ✅ 数据源列表展示
- ✅ 数据源CRUD操作
- ✅ 数据源连接测试
- ✅ 支持多种数据源类型（MySQL、Hive、ClickHouse等）

#### 页面组件
- `DatasourceList.tsx` - 数据源列表
- `DatasourceForm.tsx` - 数据源表单
- `DatasourceTest.tsx` - 连接测试组件

#### 技术要点
- 根据数据源类型动态显示表单字段
- 连接测试结果实时反馈（Loading状态 + 结果提示）
- 密码字段脱敏展示

### 2.4 任务实例管理模块

#### 功能点
- ✅ 实例列表展示（分页、搜索、筛选）
- ✅ 实例详情查看
- ✅ 实例日志查看（实时滚动）
- ✅ 实例DAG图展示
- ✅ 实例操作（重跑、取消）
- ✅ 状态实时更新（轮询或WebSocket）

#### 页面组件
- `InstanceList.tsx` - 实例列表
- `InstanceDetail.tsx` - 实例详情
- `InstanceLog.tsx` - 日志查看器
- `InstanceDagGraph.tsx` - 实例DAG图

#### 技术要点

**实时日志查看**:
```typescript
// 日志查看器组件
const LogViewer: React.FC = () => {
  const [logs, setLogs] = useState<string[]>([]);
  const logContainerRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    // 自动滚动到底部
    if (logContainerRef.current) {
      logContainerRef.current.scrollTop = logContainerRef.current.scrollHeight;
    }
  }, [logs]);
  
  // 日志关键字高亮
  const highlightLog = (log: string) => {
    return log
      .replace(/ERROR/g, '<span class="error">ERROR</span>')
      .replace(/WARN/g, '<span class="warn">WARN</span>')
      .replace(/INFO/g, '<span class="info">INFO</span>');
  };
  
  return (
    <div ref={logContainerRef} className="log-container">
      {logs.map((log, index) => (
        <div key={index} dangerouslySetInnerHTML={{ __html: highlightLog(log) }} />
      ))}
    </div>
  );
};
```

**状态实时更新**:
```typescript
// 使用轮询或WebSocket
const useInstanceStatus = (instanceId: string) => {
  const [status, setStatus] = useState<string>('');
  
  useEffect(() => {
    // 轮询方式（简单）
    const interval = setInterval(async () => {
      const response = await getInstanceDetail(instanceId);
      setStatus(response.data.status);
      
      // 如果状态为终态，停止轮询
      if (['SUCCESS', 'FAILED', 'CANCELED'].includes(response.data.status)) {
        clearInterval(interval);
      }
    }, 10000); // 10秒轮询一次
    
    return () => clearInterval(interval);
  }, [instanceId]);
  
  return status;
};
```

### 2.5 服务器管理模块

#### 功能点
- ✅ 服务器列表展示
- ✅ 服务器详情查看
- ✅ 服务器资源监控（CPU、内存、磁盘）
- ✅ 服务器状态实时更新
- ✅ 运行任务列表展示

#### 页面组件
- `ServerList.tsx` - 服务器列表
- `ServerDetail.tsx` - 服务器详情
- `ServerMonitor.tsx` - 资源监控图表

#### 技术要点

**资源监控图表**:
```typescript
// 使用 ECharts
import * as echarts from 'echarts';

const ServerMonitor: React.FC = () => {
  const chartRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    if (chartRef.current) {
      const chart = echarts.init(chartRef.current);
      
      const option = {
        title: { text: 'CPU使用率' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: timeData },
        yAxis: { type: 'value', max: 100 },
        series: [{
          name: 'CPU',
          type: 'line',
          data: cpuData,
          smooth: true
        }]
      };
      
      chart.setOption(option);
    }
  }, [cpuData, timeData]);
  
  return <div ref={chartRef} style={{ width: '100%', height: '300px' }} />;
};
```

---

## 3. 通用组件设计

### 3.1 代码编辑器组件

```typescript
// src/components/CodeEditor/index.tsx
interface CodeEditorProps {
  value: string;
  onChange: (value: string) => void;
  language: 'sql' | 'shell' | 'python';
  height?: string;
  readOnly?: boolean;
}

const CodeEditor: React.FC<CodeEditorProps> = ({ 
  value, 
  onChange, 
  language, 
  height = '400px',
  readOnly = false 
}) => {
  return (
    <Editor
      height={height}
      language={language}
      theme="vs-dark"
      value={value}
      onChange={(val) => onChange(val || '')}
      options={{
        readOnly,
        minimap: { enabled: false },
        fontSize: 14,
        lineNumbers: 'on',
        automaticLayout: true
      }}
    />
  );
};
```

### 3.2 DAG图组件

```typescript
// src/components/DagGraph/index.tsx
interface DagNode {
  id: string;
  label: string;
  type: string;
  status?: string;
}

interface DagEdge {
  source: string;
  target: string;
  type: string;
}

interface DagGraphProps {
  nodes: DagNode[];
  edges: DagEdge[];
  onNodeClick?: (node: DagNode) => void;
}

const DagGraph: React.FC<DagGraphProps> = ({ nodes, edges, onNodeClick }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const graphRef = useRef<Graph | null>(null);
  
  useEffect(() => {
    if (containerRef.current && !graphRef.current) {
      graphRef.current = new Graph({
        container: containerRef.current,
        width: containerRef.current.offsetWidth,
        height: 600,
        layout: {
          type: 'dagre',
          rankdir: 'TB',
          nodesep: 30,
          ranksep: 50
        },
        modes: {
          default: ['drag-canvas', 'zoom-canvas', 'drag-node']
        },
        defaultNode: {
          type: 'rect',
          size: [120, 40],
          style: {
            fill: '#5B8FF9',
            stroke: '#5B8FF9',
            radius: 5
          },
          labelCfg: {
            style: {
              fill: '#fff',
              fontSize: 12
            }
          }
        },
        defaultEdge: {
          type: 'polyline',
          style: {
            stroke: '#ccc',
            endArrow: true
          }
        }
      });
      
      // 节点点击事件
      graphRef.current.on('node:click', (evt) => {
        const node = evt.item?.getModel() as DagNode;
        onNodeClick?.(node);
      });
    }
    
    // 更新数据
    if (graphRef.current) {
      graphRef.current.data({ nodes, edges });
      graphRef.current.render();
    }
  }, [nodes, edges]);
  
  return <div ref={containerRef} style={{ width: '100%', height: '600px' }} />;
};
```

### 3.3 日志查看器组件

```typescript
// src/components/LogViewer/index.tsx
interface LogViewerProps {
  logs: string[];
  loading?: boolean;
  autoScroll?: boolean;
}

const LogViewer: React.FC<LogViewerProps> = ({ 
  logs, 
  loading = false,
  autoScroll = true 
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    if (autoScroll && containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight;
    }
  }, [logs, autoScroll]);
  
  const highlightLog = (log: string) => {
    let highlighted = log;
    highlighted = highlighted.replace(/ERROR/g, '<span class="log-error">ERROR</span>');
    highlighted = highlighted.replace(/WARN/g, '<span class="log-warn">WARN</span>');
    highlighted = highlighted.replace(/INFO/g, '<span class="log-info">INFO</span>');
    return highlighted;
  };
  
  return (
    <div className="log-viewer">
      {loading && <Spin />}
      <div ref={containerRef} className="log-container">
        {logs.map((log, index) => (
          <div 
            key={index} 
            className="log-line"
            dangerouslySetInnerHTML={{ __html: `${index + 1}  ${highlightLog(log)}` }}
          />
        ))}
      </div>
    </div>
  );
};
```

---

## 4. API接口封装

### 4.1 Axios 请求封装

```typescript
// src/api/request.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { message } from 'antd';

// 统一响应格式
interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message: msg, data } = response.data;
    
    if (code === 200) {
      return data;
    } else {
      message.error(msg || '请求失败');
      return Promise.reject(new Error(msg || '请求失败'));
    }
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message.error('未授权，请登录');
          // 跳转到登录页
          break;
        case 403:
          message.error('拒绝访问');
          break;
        case 404:
          message.error('请求地址不存在');
          break;
        case 500:
          message.error('服务器错误');
          break;
        default:
          message.error('请求失败');
      }
    } else {
      message.error('网络错误');
    }
    return Promise.reject(error);
  }
);

export default request;
```

### 4.2 API接口定义

```typescript
// src/api/task.ts
import request from './request';

// 任务类型定义
export interface Task {
  id: number;
  taskName: string;
  taskType: string;
  taskCode: string;
  description?: string;
  directoryId: number;
  owner: string;
  cronExpr?: string;
  timeout: number;
  status: string;
  datasourceId?: number;
  createTime: string;
  updateTime: string;
}

// 查询任务列表
export const getTaskList = (params: {
  pageNum: number;
  pageSize: number;
  taskName?: string;
  taskType?: string;
  status?: string;
  owner?: string;
}) => {
  return request.get<any, { content: Task[]; totalElements: number }>('/v1/tasks', { params });
};

// 查询任务详情
export const getTaskDetail = (id: number) => {
  return request.get<any, Task>(`/v1/tasks/${id}`);
};

// 创建任务
export const createTask = (data: Partial<Task>) => {
  return request.post<any, Task>('/v1/tasks', data);
};

// 更新任务
export const updateTask = (id: number, data: Partial<Task>) => {
  return request.put<any, Task>(`/v1/tasks/${id}`, data);
};

// 删除任务
export const deleteTask = (id: number) => {
  return request.delete(`/v1/tasks/${id}`);
};

// 查询任务DAG图
export const getTaskDag = (id: number) => {
  return request.get<any, { nodes: any[]; edges: any[] }>(`/v1/tasks/${id}/dag`);
};

// 查询任务依赖
export const getTaskDependencies = (id: number) => {
  return request.get<any, any[]>(`/v1/tasks/${id}/dependencies`);
};

// 添加任务依赖
export const addTaskDependency = (id: number, data: {
  dependTaskId: number;
  dependType: string;
  cycleOffset: number;
}) => {
  return request.post(`/v1/tasks/${id}/dependencies`, data);
};

// 删除任务依赖
export const deleteTaskDependency = (id: number, depId: number) => {
  return request.delete(`/v1/tasks/${id}/dependencies/${depId}`);
};
```

---

## 5. 路由设计

```typescript
// src/App.tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './pages/Layout';
import DirectoryPage from './pages/Directory';
import TaskListPage from './pages/Task/List';
import TaskFormPage from './pages/Task/Form';
import TaskDetailPage from './pages/Task/Detail';
import DatasourceListPage from './pages/Datasource/List';
import DatasourceFormPage from './pages/Datasource/Form';
import InstanceListPage from './pages/Instance/List';
import InstanceDetailPage from './pages/Instance/Detail';
import ServerListPage from './pages/Server/List';
import ServerDetailPage from './pages/Server/Detail';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/tasks" replace />} />
          
          {/* 任务管理 */}
          <Route path="directories" element={<DirectoryPage />} />
          <Route path="tasks" element={<TaskListPage />} />
          <Route path="tasks/create" element={<TaskFormPage />} />
          <Route path="tasks/:id/edit" element={<TaskFormPage />} />
          <Route path="tasks/:id" element={<TaskDetailPage />} />
          
          {/* 数据源管理 */}
          <Route path="datasources" element={<DatasourceListPage />} />
          <Route path="datasources/create" element={<DatasourceFormPage />} />
          <Route path="datasources/:id/edit" element={<DatasourceFormPage />} />
          
          {/* 实例管理 */}
          <Route path="instances" element={<InstanceListPage />} />
          <Route path="instances/:id" element={<InstanceDetailPage />} />
          
          {/* 服务器管理 */}
          <Route path="servers" element={<ServerListPage />} />
          <Route path="servers/:id" element={<ServerDetailPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
```

---

## 6. 状态管理设计

使用 Redux Toolkit 进行全局状态管理，配合 React Query 进行服务端状态管理。

```typescript
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import userReducer from './slices/userSlice';
import appReducer from './slices/appSlice';

export const store = configureStore({
  reducer: {
    user: userReducer,
    app: appReducer
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

---

## 7. 实施步骤

### Phase 1: 项目初始化（预计1天）

1. ✅ 创建 React + TypeScript + Vite 项目
2. ✅ 配置 ESLint、Prettier
3. ✅ 安装依赖包（Ant Design、Router、Axios等）
4. ✅ 配置路由和基础布局
5. ✅ 封装 Axios 请求
6. ✅ 配置环境变量

### Phase 2: 基础组件开发（预计2天）

1. ✅ 开发代码编辑器组件（Monaco Editor）
2. ✅ 开发 DAG 图组件（AntV G6）
3. ✅ 开发日志查看器组件
4. ✅ 开发通用表格组件
5. ✅ 开发通用表单组件

### Phase 3: 任务管理模块（预计3天）

1. ✅ 任务目录管理页面
2. ✅ 任务列表页面（搜索、分页、排序）
3. ✅ 任务创建/编辑表单
4. ✅ 任务依赖管理
5. ✅ 任务 DAG 图展示

### Phase 4: 数据源管理模块（预计1天）

1. ✅ 数据源列表页面
2. ✅ 数据源创建/编辑表单
3. ✅ 数据源连接测试

### Phase 5: 实例管理模块（预计2天）

1. ✅ 实例列表页面（搜索、筛选）
2. ✅ 实例详情页面
3. ✅ 实例日志查看器（实时日志）
4. ✅ 实例 DAG 图展示
5. ✅ 实例操作（重跑、取消）

### Phase 6: 服务器管理模块（预计2天）

1. ✅ 服务器列表页面
2. ✅ 服务器详情页面
3. ✅ 服务器资源监控图表（ECharts）
4. ✅ 运行任务列表

### Phase 7: 联调测试（预计2天）

1. ✅ 前后端接口联调
2. ✅ 功能测试
3. ✅ 兼容性测试
4. ✅ 性能优化

### Phase 8: 文档与部署（预计1天）

1. ✅ 编写前端技术文档
2. ✅ 编写部署文档
3. ✅ 提交到 GitHub

**总计**: 约 14 天

---

## 8. 技术难点与解决方案

### 8.1 DAG 图性能优化

**问题**: 大规模 DAG 图（1000+ 节点）渲染性能问题

**解决方案**:
- 使用虚拟滚动
- 分层加载（默认只显示 ±2 层依赖）
- 节点懒加载
- 使用 WebWorker 进行布局计算

### 8.2 实时日志展示

**问题**: 大量日志实时推送导致页面卡顿

**解决方案**:
- 虚拟滚动
- 日志批量渲染（每 100ms 渲染一次）
- 限制日志行数（最多显示 10000 行）
- 使用 `shouldComponentUpdate` 优化渲染

### 8.3 状态实时更新

**问题**: 多个实例状态需要实时更新

**解决方案**:
- 短轮询（10秒轮询一次）
- 只轮询运行中的实例
- 使用 React Query 的自动重新请求功能
- WebSocket（可选，后期优化）

---

## 9. 测试计划

### 9.1 单元测试

- 使用 Jest + React Testing Library
- 测试覆盖率目标: 60%+
- 重点测试工具函数和关键组件

### 9.2 集成测试

- 测试前后端接口联调
- 测试核心业务流程

### 9.3 E2E 测试

- 使用 Cypress（可选）
- 测试关键用户路径

---

## 10. 部署方案

### 10.1 开发环境

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 访问: http://localhost:5173
```

### 10.2 生产环境

```bash
# 构建
pnpm build

# 生成的 dist 目录部署到 Nginx
nginx -c /path/to/nginx.conf
```

**Nginx 配置**:

```nginx
server {
    listen 80;
    server_name scheduler.example.com;
    
    root /var/www/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 代理后端API
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 11. 关键配置文件

### 11.1 package.json

```json
{
  "name": "dw-scheduler-management-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "format": "prettier --write \"src/**/*.{ts,tsx,css,less}\""
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "antd": "^5.12.0",
    "@ant-design/icons": "^5.2.6",
    "axios": "^1.6.2",
    "@reduxjs/toolkit": "^2.0.1",
    "react-redux": "^9.0.4",
    "@tanstack/react-query": "^5.12.0",
    "@monaco-editor/react": "^4.6.0",
    "@antv/g6": "^4.8.24",
    "echarts": "^5.4.3",
    "echarts-for-react": "^3.0.2",
    "dayjs": "^1.11.10"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@typescript-eslint/eslint-plugin": "^6.14.0",
    "@typescript-eslint/parser": "^6.14.0",
    "@vitejs/plugin-react": "^4.2.1",
    "eslint": "^8.55.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "eslint-plugin-react-refresh": "^0.4.5",
    "less": "^4.2.0",
    "prettier": "^3.1.1",
    "typescript": "^5.2.2",
    "vite": "^5.0.8"
  }
}
```

### 11.2 vite.config.ts

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  css: {
    preprocessorOptions: {
      less: {
        javascriptEnabled: true
      }
    }
  }
});
```

### 11.3 tsconfig.json

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

---

## 12. 风险与对策

| 风险 | 影响 | 对策 |
|-----|------|------|
| DAG图渲染性能问题 | 用户体验差 | 分层加载、虚拟渲染 |
| 实时日志卡顿 | 页面假死 | 批量渲染、限制行数 |
| API接口变更 | 功能异常 | 与后端保持沟通 |
| 浏览器兼容性 | 部分用户无法使用 | Polyfill、降级方案 |
| 开发进度延期 | 交付延迟 | 合理排期、及时沟通 |

---

**文档结束**

> 本文档版本: V1.0
> 最后更新时间: 2026-01-21
> 文档维护: Claude
