<!-- Generated: 2026-03-22 | Files scanned: 2 | Token estimate: ~300 -->

# Dependencies

## Backend (pom.xml)

| Dependency | Version | Purpose |
|-----------|---------|---------|
| spring-boot-starter-web | 2.7.18 | REST API |
| spring-boot-starter-security | 2.7.18 | Auth + RBAC |
| spring-boot-starter-validation | 2.7.18 | @Valid DTO |
| spring-boot-starter-data-redis | 2.7.18 | Cache |
| mybatis-plus-boot-starter | 3.5.3.1 | ORM |
| mysql-connector-java | 8.0.33 | DB driver |
| jjwt | 0.9.1 | JWT tokens |
| hutool-all | 5.8.22 | Utility lib |
| easyexcel | 3.3.2 | Excel import |
| lombok | managed | Boilerplate |

## Frontend (package.json)

| Dependency | Version | Purpose |
|-----------|---------|---------|
| vue | 2.6.14 | Framework |
| vue-router | 3.5.3 | Routing |
| vuex | 3.6.2 | State management |
| element-ui | 2.15.13 | UI components |
| axios | 1.4.0 | HTTP client |
| echarts | 5.4.2 | Charts (Dashboard, Statistics, Results) |
| js-cookie | 3.0.5 | Token persistence |

## External Services

| Service | Port | Purpose |
|---------|------|---------|
| MySQL | 3306 | Primary data store |
| Redis | 6379 | Cache / session |

## Unused Entities (no service/controller)
- Position — only referenced via positionId FK
- Application — only used for count in AdminStatisticsService
