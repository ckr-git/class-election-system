# 班级干部评选系统 - 前端

## 技术栈

- Vue 2.6
- Vue Router 3.5
- Vuex 3.6
- Element UI 2.15
- ECharts 5.4
- Axios 1.4

## 项目结构

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── api/                  # API接口
│   ├── assets/               # 静态资源
│   ├── components/           # 公共组件
│   ├── layout/               # 布局组件
│   │   ├── UserLayout.vue   # 用户端布局
│   │   └── AdminLayout.vue  # 管理端布局
│   ├── router/               # 路由配置
│   ├── store/                # Vuex状态管理
│   ├── utils/                # 工具类
│   ├── views/                # 页面组件
│   │   ├── user/            # 用户端页面
│   │   └── admin/           # 管理端页面
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── package.json
├── vue.config.js
└── README.md
```

## 快速开始

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run serve
```

### 生产构建
```bash
npm run build
```

## 功能模块

### 用户端
- 登录注册
- 首页概览
- 选举列表
- 候选人浏览
- 在线投票
- 投票结果查询
- 我的申请记录
- 个人中心

### 管理端
- 仪表盘（数据可视化）
- 用户管理
- 选举管理
- 候选人审核
- 数据统计

## 访问地址

- 开发环境：http://localhost:8081
- 后端API：http://localhost:8080/api

## 默认账号

### 管理员
- 用户名：admin
- 密码：admin123

### 学生
- 需自行注册

## 注意事项

1. 确保后端服务已启动
2. 修改 `vue.config.js` 中的代理地址（如果需要）
3. 首次运行需安装依赖
