# 班级干部评选系统 — 自动化测试报告

**测试时间**: 2026-04-20
**技术栈**: Spring Boot 2.7.18 + Vue 2 + Element UI + MyBatis-Plus + MySQL + JWT
**测试模式**: full (gate + sweep + polish)

---

## 总览

| 指标 | 数值 |
|------|------|
| 业务闭环 | 4/4 通过 |
| 功能分支 | 12/13 完成 (1 skip) |
| Twig 测试点 | 30 pass, 5 fixed, 1 skip, 0 fail |
| API 端点 | 23/26 通过 (1 fixed, 2 skip) |
| 发布状态 | **ready** |

---

## 发现并修复的问题 (5 fixed)

### 后端 (2)

1. **投票接口未校验候选人归属** (HIGH)
   - `VoteService.submitVote()` 未验证 candidateId 是否属于 electionId
   - 修复: 添加候选人-选举关联校验
   - 文件: `src/main/java/com/election/system/service/VoteService.java`

2. **全局异常处理器吞掉异常** (MEDIUM)
   - `GlobalExceptionHandler.handleException()` 返回"系统异常"但不记录日志
   - 修复: 添加 `log.error("Unhandled exception", e)`
   - 文件: `src/main/java/com/election/system/common/GlobalExceptionHandler.java`

### 前端 (3)

3. **选举创建/编辑表单无客户端验证** (MEDIUM)
   - 空表单可直接提交到后端
   - 修复: 添加 el-form rules (标题、描述、时间必填)
   - 文件: `frontend/src/views/admin/Elections.vue`

4. **用户管理表单无客户端验证** (MEDIUM)
   - 同上，新增用户表单无验证
   - 修复: 添加 el-form rules (学号、姓名、密码必填)
   - 文件: `frontend/src/views/admin/Users.vue`

5. **投票页无选举时显示异常** (LOW)
   - 直接访问 /vote 显示 "在线投票 -" + 空表格
   - 修复: 添加引导提示和跳转按钮
   - 文件: `frontend/src/views/user/Vote.vue`

6. **我的申请页时间未格式化** (LOW)
   - 申请时间显示原始 ISO 格式 "2025-12-26T23:53:03"
   - 修复: 使用 formatTime 工具函数
   - 文件: `frontend/src/views/user/MyApplications.vue`

---

## 已知问题 (不阻塞发布)

- favicon.ico 404 (缺少网站图标)
- Redis 配置存在但未使用 (可移除依赖)
- 分页参数未做服务端校验 (page=0 被接受, size=10000 未限制)

---

## 测试资产

| 资产 | 路径 |
|------|------|
| E2E 测试 | `tests/e2e/auth.spec.js`, `tests/e2e/admin-elections.spec.js` |
| API 测试 | `tests/api/api-test.sh` |
| 冒烟测试 | `scripts/smoke-test.sh` |

---

## 页面覆盖

| 页面 | 状态 | 关键发现 |
|------|------|---------|
| 登录/注册 | pass | 验证正常，路由守卫有效 |
| 用户首页 | pass | 统计卡片和快捷操作正常 |
| 选举列表 | pass | 状态标签、操作按钮正确 |
| 候选人列表 | pass | 卡片布局正常 |
| 投票页 | fixed | 无选举时引导优化 |
| 投票结果 | pass | 选举选择器正常 |
| 我的申请 | fixed | 时间格式化修复 |
| 管理员仪表盘 | pass | 统计数据和图表正常 |
| 管理员用户管理 | fixed | 表单验证添加 |
| 管理员选举管理 | fixed | 表单验证添加 |
| 管理员候选人管理 | pass | 列表和筛选正常 |
| 管理员数据统计 | pass | 概览和选举选择器正常 |
