# 班级干部评选系统

## 项目简介

这是一个基于SpringBoot + Vue2 + Element UI + SpringSecurity开发的班级干部竞选投票系统，支持学生端和管理员端，提供完整的竞选报名、在线投票、申请审核、数据统计等功能。

## 技术栈

### 后端技术
- **架构**: B/S、MVC
- **JDK版本**: JDK8+
- **框架**: SpringBoot 2.7.18
- **安全框架**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **定时任务**: Spring Schedule
- **工具类**: Hutool

### 前端技术
- **框架**: Vue 2
- **UI库**: Element UI
- **数据可视化**: ECharts
- **HTTP客户端**: Axios

## 功能模块

### 用户（学生）端功能

#### 登录注册
- 账号密码认证
- 个人信息管理
- 修改密码

#### 班干部竞选报名
- 查看可报名的选举活动
- 填写竞选信息（口号、简介、成就）
- 上传竞选照片
- 提交竞选申请

#### 查看候选人
- 浏览所有候选人信息
- 查看候选人详细资料
- 按职位筛选候选人

#### 在线投票
- 查看投票时间和规则
- 为候选人投票
- 查看投票进度

#### 投票结果查询
- 查看实时票数统计
- 查看最终选举结果

#### 查看个人申请记录
- 查看申请历史
- 查看审核状态和意见

#### 查看公告
- 系统公告
- 选举相关通知

#### 反馈意见
- 提交建议、投诉、咨询
- 查看反馈处理结果

#### 个人中心
- 个人资料管理
- 修改密码

### 管理员端功能

#### 仪表盘数据可视化统计
- 投票统计图表（ECharts）
- 候选人统计
- 参与度分析
- 实时数据监控

#### 用户管理
- 用户列表查看
- 用户信息维护
- 批量导入用户
- 重置密码
- 账号禁用/启用

#### 班级管理
- 班级信息管理
- 学生分配
- 班级统计

#### 职位管理
- 职位信息配置
- 职位职责说明
- 职位人数设置

#### 候选人管理
- 候选人列表
- 候选人资格审核
- 候选人信息维护
- 票数统计

#### 投票管理
- 选举活动创建
- 投票规则设置（时间范围、票数限制、班级指定）
- 选举状态管理
- 投票记录查看
- 防止作弊检测

#### 申请审核
- 竞选申请审批流程
- 审核意见填写
- 批量审核

#### 权限管理
- 角色管理
- 权限配置
- 菜单权限控制
- 按钮权限控制

#### 角色分配
- 用户角色分配
- 批量分配角色
- RBAC权限控制模型

#### 公告管理
- 发布系统公告
- 发布选举通知
- 公告编辑/删除
- 优先级设置

#### 反馈处理
- 查看用户反馈
- 回复处理反馈
- 反馈状态管理

#### 个人中心
- 管理员信息管理

## 系统亮点

1. ✅ **完整的业务流程**: 学生申请竞选班干部 → 管理员审核申请 → 发起职位投票 → 学生参与投票 → 自动统计结果 → 公布班干部名单

2. ✅ **全面的竞选管理体系**: 支持学生填写个人资料、上传头像、填写竞选口号和成就列表；管理员多维度审核机制；灵活投票规则设置（时间范围、票数限制、班级指定）

3. ✅ **RBAC权限控制模型**: 基于角色的访问控制，实现菜单权限、按钮权限和数据权限的精细化管理

4. ✅ **实时投票统计**: 使用ECharts图表展示投票结果，支持饼图、柱状图等多种可视化方式

5. ✅ **定时任务管理**: 自动结束过期投票、定期数据统计，保证系统运行效率

6. ✅ **安全可靠**: 使用SpringSecurity框架+JWT+密码加密保障系统安全，防止投票作弊

## 项目结构

```
班级干部评选系统/
├── src/
│   ├── main/
│   │   ├── java/com/election/system/
│   │   │   ├── ElectionSystemApplication.java  # 主启动类
│   │   │   ├── common/                          # 通用类
│   │   │   ├── config/                          # 配置类
│   │   │   ├── security/                        # Security配置
│   │   │   ├── controller/                      # 控制器
│   │   │   ├── entity/                          # 实体类
│   │   │   ├── mapper/                          # Mapper接口
│   │   │   ├── service/                         # 服务层
│   │   │   └── scheduled/                       # 定时任务
│   │   └── resources/
│   │       ├── application.yml                  # 配置文件
│   │       └── mapper/                          # MyBatis XML
│   └── test/                                     # 测试
├── frontend/                                     # Vue2前端项目
│   ├── src/
│   │   ├── api/                                  # API接口
│   │   ├── router/                               # 路由配置
│   │   ├── store/                                # Vuex状态管理
│   │   ├── utils/                                # 工具类
│   │   └── views/                                # 页面组件
│   │       ├── Login.vue                         # 登录页
│   │       ├── Register.vue                      # 注册页
│   │       ├── user/                             # 学生端页面
│   │       │   ├── Home.vue                      # 首页
│   │       │   ├── Elections.vue                 # 选举列表
│   │       │   ├── Candidates.vue                # 候选人列表
│   │       │   ├── Vote.vue                      # 投票页面
│   │       │   ├── Results.vue                   # 投票结果
│   │       │   ├── MyApplications.vue            # 我的申请
│   │       │   └── Profile.vue                   # 个人中心
│   │       └── admin/                            # 管理端页面
│   │           ├── Dashboard.vue                 # 仪表盘
│   │           ├── Users.vue                     # 用户管理
│   │           ├── Elections.vue                 # 选举管理
│   │           ├── Candidates.vue                # 候选人管理
│   │           └── Statistics.vue                # 数据统计
├── database.sql                                  # 数据库SQL脚本
├── pom.xml                                       # Maven配置
└── README.md                                     # 项目文档
```

## 数据库表结构

主要数据表（共14张）:
- `user` - 用户表
- `class` - 班级表
- `position` - 职位表
- `election` - 选举活动表
- `candidate` - 候选人表
- `vote_record` - 投票记录表
- `application` - 申请记录表
- `announcement` - 公告表
- `feedback` - 反馈意见表
- `role` - 角色表
- `permission` - 权限表
- `role_permission` - 角色权限关联表
- `user_role` - 用户角色关联表
- `system_log` - 系统日志表

## 快速开始

### 环境要求
- JDK 17+ (推荐，已测试通过)
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+（可选）
- Node.js 14+ (用于前端)

### 后端启动

1. **导入数据库**
```bash
mysql -u root -p < database.sql
```

2. **修改配置**
```yaml
# 修改src/main/resources/application.yml中的数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/election_system
    username: root
    password: your_password
```

3. **启动项目**
```bash
# 方式1: Maven命令
mvn spring-boot:run

# 方式2: IDEA直接运行ElectionSystemApplication类
```

4. **访问接口**
```
后端API: http://localhost:8080/api
```

### 前端启动

```bash
cd frontend
npm install
npm run serve
```

访问: http://localhost:8081

## 初始化数据

### 创建管理员账号
```sql
INSERT INTO `user` (username, password, nickname, role, status) 
VALUES ('admin', 'ENCRYPTED_PASSWORD', '系统管理员', 'ADMIN', 1);
```

### 创建班级
```sql
INSERT INTO `class` (name, grade, major, teacher) VALUES 
('计算机1班', '2023级', '计算机科学与技术', '张老师'),
('计算机2班', '2023级', '计算机科学与技术', '李老师');
```

### 创建职位
```sql
INSERT INTO `position` (name, description, max_count, sort_order) VALUES 
('班长', '负责班级全面工作', 1, 1),
('副班长', '协助班长工作', 2, 2),
('学习委员', '负责学习相关事务', 1, 3),
('生活委员', '负责班级生活事务', 1, 4),
('文艺委员', '负责文艺活动', 1, 5);
```

## API接口文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 候选人接口
- `POST /api/candidate/apply` - 申请成为候选人
- `GET /api/candidate/list` - 查看候选人列表
- `GET /api/candidate/{id}` - 获取候选人详情

### 投票接口
- `POST /api/vote/submit` - 提交投票
- `GET /api/vote/result/{electionId}` - 查看投票结果
- `GET /api/vote/my` - 查看我的投票记录

## 注意事项

1. 首次运行前请确保MySQL服务已启动
2. 数据库密码请根据实际情况修改
3. JWT密钥建议在生产环境中使用更复杂的字符串
4. 投票时间设置要合理，确保学生有足够时间参与
5. 建议使用Redis提升系统性能

## 后续优化方向

1. 添加短信/邮件通知功能
2. 支持多轮投票
3. 添加候选人演讲视频
4. 增加投票提醒功能
5. 优化移动端体验
6. 添加数据导出功能
7. 集成第三方登录

## 技术支持

如有问题，欢迎提Issue或PR。

## 许可证

MIT License

## 已实现功能清单

### 后端API (已全部实现)
| 模块 | 接口 | 状态 |
|------|------|------|
| 认证 | 登录/注册/登出 | ✅ |
| 选举 | 列表/详情/创建/状态管理 | ✅ |
| 候选人 | 申请/列表/审核/删除 | ✅ |
| 投票 | 提交/结果/我的记录 | ✅ |
| 用户管理 | CRUD/重置密码/禁用 | ✅ |
| 数据统计 | 仪表盘/选举统计 | ✅ |

### 前端页面 (已全部实现)
| 页面 | 功能 | 状态 |
|------|------|------|
| 登录/注册 | 用户认证 | ✅ |
| 首页 | 统计卡片展示 | ✅ |
| 选举列表 | 查看/报名/投票入口 | ✅ |
| 投票页面 | 候选人列表/投票操作 | ✅ |
| 投票结果 | 饼图/表格展示 | ✅ |
| 我的申请 | 申请记录列表 | ✅ |
| 个人中心 | 信息展示/修改密码 | ✅ |
| 管理仪表盘 | 统计卡片/图表 | ✅ |
| 用户管理 | 用户CRUD操作 | ✅ |
| 选举管理 | 创建/状态流转 | ✅ |
| 候选人管理 | 审核/删除 | ✅ |
| 数据统计 | 统计卡片/柱状图 | ✅ |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | hackeradmin | hack123 |
| 学生 | 2023030721 | 123456 |

## 选举流程

```
1. 管理员创建选举 (状态: 未开始)
       ↓
2. 管理员开始报名 (状态: 报名中)
       ↓
3. 学生报名成为候选人
       ↓
4. 管理员审核候选人 (通过/拒绝)
       ↓
5. 管理员开始投票 (状态: 投票中)
       ↓
6. 学生进行投票
       ↓
7. 管理员结束投票 (状态: 已结束)
       ↓
8. 查看投票结果
```
