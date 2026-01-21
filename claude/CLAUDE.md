# Claude Code 项目约束

本文件用于设置 Claude 在此项目中的工作约束和指导原则。

## 代码规范

### 命名规范
- **类名**: 使用 PascalCase（大驼峰），例如 `UserController`, `BusinessException`
- **方法名**: 使用 camelCase（小驼峰），例如 `getUserById`, `createUser`
- **变量名**: 使用 camelCase，例如 `userId`, `userName`
- **常量名**: 使用 UPPER_SNAKE_CASE，例如 `MAX_SIZE`, `DEFAULT_TIMEOUT`
- **包名**: 全小写，例如 `com.company.ba.controller`

### 注释规范
- 所有 public 方法必须添加 Javadoc 注释
- 复杂的业务逻辑需要添加行内注释说明
- 注释使用中文，保持简洁清晰

### 代码风格
- 使用 Lombok 注解简化代码（@Data, @RequiredArgsConstructor 等）
- Controller 层只负责参数接收和响应返回
- Service 层处理业务逻辑，需要事务的方法添加 @Transactional
- 统一使用 Result 包装所有 API 响应

##前端约束
- 框架选择：选择稳定版本的React框架
- 业务逻辑：前端只做业务展示和交互，所有业务逻辑下沉到后端，包含Option的枚举值

## 项目架构约束

### 分层架构
```
controller -> service -> repository -> entity
                ↓
              dto
```

### 包结构
- `controller`: REST API 控制器
- `service`: 业务逻辑层
- `repository`: 数据访问层（JPA Repository）
- `entity`: 数据库实体类
- `dto`: 数据传输对象
- `config`: 配置类
- `common.response`: 统一响应对象
- `common.exception`: 异常类

## 开发约束

### 新功能开发流程
1. 先创建 Entity 实体类（如果需要新表）
2. 创建 Repository 接口
3. 创建 DTO 类（用于前后端数据传输）
4. 创建 Service 类（业务逻辑）
5. 创建 Controller 类（API 接口）

### 异常处理
- 业务异常使用 `BusinessException`
- 不要在 Controller 中处理异常，统一由 `GlobalExceptionHandler` 处理
- 异常信息要清晰明确，便于前端展示

### 数据库操作
- 使用 JPA 进行数据库操作
- 实体类需要添加 `@CreationTimestamp` 和 `@UpdateTimestamp` 字段
- 删除操作考虑使用逻辑删除（添加 deleted 字段）

### API 设计
- 遵循 RESTful 规范
- GET: 查询操作
- POST: 创建操作
- PUT: 更新操作
- DELETE: 删除操作
- 统一使用 `/api` 作为基础路径

### 安全规范
- 敏感信息（密码等）不能明文存储
- API 接口需要添加参数校验
- 避免 SQL 注入、XSS 等安全漏洞

## 禁止事项

- ❌ 不要在 Controller 中编写业务逻辑
- ❌ 不要直接返回 Entity，使用 DTO 进行数据传输
- ❌ 不要在代码中硬编码配置信息，使用 application.yml
- ❌ 不要提交包含真实密码的配置文件
- ❌ 不要删除或修改已有的通用工具类和配置类

## 测试要求

- 新增功能需要编写单元测试
- 测试类放在 `src/test/java` 对应的包下
- 测试方法命名: `testXxx` 或使用 `@DisplayName` 注解

## 文档约束

### CLAUDE必须遵循的要求
- CLAUDE每次进入项目必读，必须遵循的要求！
- CLAUDE每次进入项目必读，必须遵循的要求！
- 需要按照以下路径读取关键文档
- 请严格按照关键文档约束进行系统开发

| 文档类型 | 路径 | 用途 |
|---------|-------|----------|
| 需求文档 | claude/REQUIREMENT.md  | 了解业务需求 |
| 架构设计 | claude/Architecture.MD | 理解系统架构 |
| 工作约束 | claude/CLAUDE.md | 遵循开发规范 |
| 项目结构 | claude/PROJECT_STRUCTURE.md | 快速定位文件 |
| 实现指南 | claude/REALTIME_IMPLEMENTATION_GUIDE.md | 查看已实现功能 |
| 验证指南 | claude/BROWSER_VERIFICATION_GUIDE.md | 进行功能验证 |

### 生成文档要求
- ✅ 请先理解输入需求和需求文档，做好系统改动规划，修改架构设计文档，做好深度思考
- ✅ 需要改动系统请先更新架构设计文档（claude/Architecture.MD）
- ✅ 需要改动系统请先更新项目结构文档（claude/PROJECT_STRUCTURE.md）
- ✅ 需要改动系统请先更新实现指南文档（claude/PROJECT_STRUCTURE.md）
- ✅ 系统设计文档，说明文档请放置到项目根目录下的claude目录
- ✅ 所有.md类型的文档请放置到项目根目录下的claude目录
- ✅ 相关运行脚本请放置到项目根目录下的bin目录，做好说明
- ✅ 系统改动完成，验证完毕更新验证指南文档（claude/BROWSER_VERIFICATION_GUIDE.md）

### 文档禁止事项
- ❌ 禁止改动需求文档（claude/REQUIREMENT.md）
- ❌ 禁止将生成的md文档放置在项目根目录下，应该放置到项目下的claude目录
- ❌ 禁止随意改动工作约束文档（claude/CLAUDE.md），改动前请确认

## 待补充约束

请根据项目实际需要在此添加更多约束...
