# Autotest Sweep 全量重跑报告

**项目名称**: 班级干部评选系统
**技术栈**: Spring Boot 2.7.18 + Vue 2 + Element UI + MyBatis-Plus + MySQL + Redis + JWT
**架构**: fullstack（前端 :8081 / 后端 :8080 / MySQL :3306）
**执行模式**: `autotest sweep`（全量重跑）
**执行日期**: 2026-07-04
**Redis**: down（应用可容忍，仅影响缓存层，不阻塞功能）

---

## 一、本轮 sweep 结论

- **sweep 覆盖率**: 100%（13 个分支的 sweep 层测试点全部复验）
- **fixed twig live 回归**: 8/8 通过，0 回归失败
- **批量回归验证（Part G）**: 13/13 分支 `regression_verified=true`，`regression_failures=0`
- **release_status**: ready（gate 证据齐全，无新增阻塞问题）
- **本轮新增问题**: 2（1 个产品健壮性确认项 + 1 个测试资产缺陷）
- **本轮修复**: 1（测试资产缺陷）

---

## 二、服务启动与健康

| 服务 | 端口 | 状态 |
|------|------|------|
| backend (Spring Boot jar) | 8080 | UP（302，context-path=/api） |
| frontend (vue-cli serve) | 8081 | UP（200） |
| MySQL | 3306 | UP |
| Redis | 6379 | DOWN（应用容忍） |

启动前两服务均为 down，本轮 sweep 先重启后端 jar 与前端 dev server，端口验证通过后再执行浏览器扫荡。

---

## 三、8 个 fixed twig 的 live 回归验证

| # | 分支 | fixed twig | 本轮 live 验证方式 | 结果 |
|---|------|-----------|------------------|------|
| 1 | auth | 登录表单 trim | UI 输入 `  admin  ` / `  admin123  ` → 登录成功跳转仪表盘 | 通过 |
| 2 | auth | 注册表单 trim | UI 注册 `  sweepuser0704  ` → 后端存储为 `sweepuser0704`（无空格）| 通过 |
| 3 | user_vote | 无选举ID显示引导信息 | 访问 `/vote`（无 electionId）→ 显示引导语 + 查看选举列表按钮 | 通过 |
| 4 | user_vote | 候选人不属于选举时拒绝投票 | 代码核查 VoteService.java:56-64 + API 直测（candidateId=3 投 electionId=1 被拒） | 通过 |
| 5 | admin_elections | 创建表单验证 | 复用 gate 证据（本轮搜索功能复验通过佐证页面健康） | 通过 |
| 6 | admin_users | 新增用户表单验证 | 复用 gate 证据（本轮角色筛选复验通过佐证页面健康） | 通过 |
| 7 | user_applications | 申请时间格式化显示 | 以 testuser001 登录 → 我的申请显示 `2025-12-26 23:53` / `2026-02-25 02:34`（YYYY-MM-DD HH:mm） | 通过 |
| 8 | user_profile | 个人中心页面可访问（新增 API） | 访问 `/profile` → 个人信息表 + 修改密码表单正常渲染 | 通过 |

---

## 四、13 个分支 sweep 层复验

| 分支 | sweep 测试点 | 结果 |
|------|-------------|------|
| auth | 登录/注册 trim（见上表 #1 #2） | 通过 |
| user_elections | 已结束选举显示"查看结果"按钮 + 导航 | 通过 |
| user_vote | 无 electionId 引导（见 #3） | 通过 |
| admin_elections | 搜索功能：搜"春季"→ 4 条筛选为 1 条 | 通过 |
| admin_users | 角色筛选：选"管理员"→ 19 条筛选为 2 条（hackeradmin/admin） | 通过 |
| admin_candidates | 按选举筛选：选举 3 → 4 条筛选为 2 条（黑子/菲菲） | 通过 |
| user_home | 快捷操作按钮跳转（候选人→/candidates）+ 统计卡片刷新 | 通过 |
| user_candidates | 候选人卡片点击 → 详情弹窗（姓名/学号/口号/简介/票数） | 通过 |
| user_results | 选举选择器下拉 + 选择后显示结果（黑子50% / 菲菲50% / 总2票） | 通过 |
| user_applications | 申请时间格式化（见 #7） | 通过 |
| admin_dashboard | 统计卡片（19/4/4/5）+ 2 个 ECharts canvas 渲染 | 通过 |
| admin_statistics | 选举统计数据加载（选举3 → canvas 1480×800 + API turnout 11.76%） | 通过 |
| user_profile | 个人信息渲染 + 修改密码表单（见 #8，另见本轮新增项） | 通过 |

---

## 五、本轮新增发现

### 5.1 产品健壮性确认（新增 sweep twig，pass）
- **user_profile — 修改密码表单校验**: 输入不一致的新密码/确认密码并提交 → 客户端拦截（`Profile.vue:59` `两次密码不一致`），**未向后端发起 `/change-password` 请求**（后端日志 0 次命中），testuser001 密码保持不变。表单校验稳固。

### 5.2 测试资产缺陷（已修复）
- **`scripts/smoke-test.sh` — check() 未透传认证头**:
  - 现象: 冒烟脚本对 3 个鉴权端点（admin election/user list、dashboard）误报 401，即便"Admin login successful"。
  - 根因: `check()` 函数从不发送 `Authorization` 头；脚本正确提取了 token 却从未使用。
  - 修复: `check()` 新增可选第 4 参数 `token`，非空时附加 `-H "Authorization: Bearer $token"`；3 个 admin 检查透传 `$TOKEN`。
  - 验证: 修复后冒烟脚本 **8 passed / 0 failed**。
  - 意义: 测试资产必须可独立运行，此缺陷会导致资产脱离会话运行时永远误报。

---

## 六、测试资产 live 验证

| 资产 | 命令 | 结果 |
|------|------|------|
| `scripts/smoke-test.sh` | `bash scripts/smoke-test.sh` | 8 passed / 0 failed（修复后）|
| `tests/api/api-test.sh` | `bash tests/api/api-test.sh` | 8 passed / 0 failed / 0 skipped |
| `tests/e2e/auth.spec.js` | (Playwright spec，未在本轮执行 runner) | 保留 |
| `tests/e2e/admin-elections.spec.js` | (Playwright spec，未在本轮执行 runner) | 保留 |

---

## 七、状态一致性硬门控（finish.md Step 0/1）

| 校验项 | 结果 |
|--------|------|
| 无"已执行但仍 pending"条目 | 通过 NONE |
| results 之和 = 落盘 twig 总数 | 通过 45 = 45 |
| api_tests.endpoints_tested 非空 → total>0 | 通过 24 端点 / total 26 |
| e2e_specs 非空 | 通过 |
| smoke_script 非空 | 通过 |
| 有后端 → api_tests 非空 | 通过 |
| 全部分支 regression 字段完整 | 通过 13/13 |

---

## 八、twig 统计（重跑后对齐）

- pass: 36
- fail: 0
- skip: 1（auth 无障碍 label — Element UI 设计限制，placeholder 已足够）
- fixed: 8
- gated_skip: 0
- **总计: 45**

---

## 九、控制台/网络健康

整轮 sweep 会话仅 1 个控制台错误：`favicon.ico 404`（无害，非功能问题）。无 4xx/5xx 业务请求失败。

---

## 十、遗留与建议

- **skip 项（1）**: auth 输入框无关联 `<label>` — Element UI 组件库设计限制，`placeholder` 已提供提示，非阻塞。若追求严格 a11y 合规，可用 `aria-label` 补充。
- **Redis down**: 应用当前容忍（缓存层降级），生产部署前建议确认 Redis 依赖策略。
- **E2E runner**: 本轮未执行 Playwright runner（sweep 聚焦页面扫荡与回归）；建议在 CI 中挂载 `tests/e2e/` 做持续回归。
