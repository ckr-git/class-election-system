<!-- Generated: 2026-03-22 | Files scanned: 67 | Token estimate: ~600 -->

# Architecture

## Stack
- Backend: Spring Boot 2.7.18 + MyBatis-Plus 3.5.3 + MySQL 8 + Redis
- Frontend: Vue 2.6 + Element UI 2.15 + Vuex + ECharts
- Auth: JWT (HS512, 7d expiry, BCrypt passwords)

## Services
```
Frontend (dev:3000) → proxy /api → Backend (8080) → MySQL (3306)
                                                   → Redis (6379)
```

## Module Map
```
Auth         → register / login / JWT
Election     → CRUD + status machine (0→1→2→3)
Candidate    → apply + review + IDOR protection
Vote         → submit + atomic count + limit check
Statistics   → dashboard + per-election stats
UserImport   → Excel batch import (EasyExcel)
```

## Security
- JWT filter on all routes except /auth/**, /public/**
- @PreAuthorize("hasRole('ADMIN')") on /admin/** controllers
- XssUtil.clean() on user-facing text fields
- IDOR check: non-admin can only query own candidate applications
- Atomic vote_count increment (SQL UPDATE ... SET vote_count = vote_count + 1)

## Scheduled Tasks
- ElectionScheduledTask: auto-updates election status based on time boundaries
