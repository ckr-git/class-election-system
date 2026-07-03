<!-- Generated: 2026-03-22 | Files scanned: 38 | Token estimate: ~800 -->

# Backend

## Routes

### Auth (public)
```
POST /api/auth/register  → AuthController → AuthService.register → UserMapper
POST /api/auth/login     → AuthController → AuthService.login → UserMapper + JwtUtil
POST /api/auth/logout    → AuthController (no-op, returns success)
```

### Election (authenticated)
```
GET  /api/election/list  → ElectionController → ElectionService.getElectionList → ElectionMapper
GET  /api/election/{id}  → ElectionController → ElectionService.getElectionDetail → ElectionMapper
```

### Candidate (authenticated, IDOR-protected)
```
POST /api/candidate/apply → CandidateController → CandidateService.apply → CandidateMapper + ElectionMapper
GET  /api/candidate/list  → CandidateController → CandidateService.getCandidateList → CandidateMapper + UserMapper + ElectionMapper
GET  /api/candidate/{id}  → CandidateController → CandidateService.getCandidateDetail → CandidateMapper + UserMapper
```

### Vote (authenticated)
```
POST /api/vote/submit        → VoteController → VoteService.submitVote → VoteRecordMapper + CandidateMapper + ElectionMapper
GET  /api/vote/result/{eid}  → VoteController → VoteService.getVoteResult → CandidateMapper + UserMapper + ElectionMapper
GET  /api/vote/my            → VoteController → VoteService.getMyVotes → VoteRecordMapper
GET  /api/vote/count         → VoteController → VoteService.getVoteCount → VoteRecordMapper + ElectionMapper
```

### Admin User (ADMIN role)
```
GET    /api/admin/user/list              → AdminUserController → AdminUserService.getUserList
POST   /api/admin/user/create            → AdminUserController → AdminUserService.createUser
PUT    /api/admin/user/update            → AdminUserController → AdminUserService.updateUser
DELETE /api/admin/user/{id}              → AdminUserController → AdminUserService.deleteUser
POST   /api/admin/user/reset-password    → AdminUserController → AdminUserService.resetPassword
POST   /api/admin/user/toggle-status/{id}→ AdminUserController → AdminUserService.toggleUserStatus
POST   /api/admin/user/import            → AdminUserController → AdminUserService.importUsers (EasyExcel)
```

### Admin Election (ADMIN role)
```
GET    /api/admin/election/list          → AdminElectionController → AdminElectionService.getElectionList
POST   /api/admin/election/create        → AdminElectionController → AdminElectionService.createElection
PUT    /api/admin/election/update        → AdminElectionController → AdminElectionService.updateElection
DELETE /api/admin/election/{id}          → AdminElectionController → AdminElectionService.deleteElection (cascade candidates)
POST   /api/admin/election/change-status → AdminElectionController → AdminElectionService.changeElectionStatus
```

### Admin Candidate (ADMIN role)
```
GET    /api/admin/candidate/list    → AdminCandidateController → AdminCandidateService.getCandidateList
POST   /api/admin/candidate/review  → AdminCandidateController → AdminCandidateService.reviewCandidate
DELETE /api/admin/candidate/{id}    → AdminCandidateController → AdminCandidateService.deleteCandidate
```

### Admin Statistics (ADMIN role)
```
GET /api/admin/statistics/dashboard       → AdminStatisticsController → AdminStatisticsService.getDashboardStats
GET /api/admin/statistics/election/{eid}  → AdminStatisticsController → AdminStatisticsService.getElectionVoteStats
```

## Service → Mapper Dependencies
```
AuthService            → UserMapper
ElectionService        → ElectionMapper
CandidateService       → CandidateMapper, ElectionMapper, UserMapper
VoteService            → VoteRecordMapper, CandidateMapper, UserMapper, ElectionMapper
AdminUserService       → UserMapper
AdminElectionService   → ElectionMapper, CandidateMapper, VoteRecordMapper
AdminCandidateService  → CandidateMapper, UserMapper
AdminStatisticsService → UserMapper, ElectionMapper, CandidateMapper, VoteRecordMapper, ApplicationMapper
```

## Key Business Rules
- Vote: checks election status=2, time window, duplicate vote, voteLimit
- Candidate apply: checks election status=1, apply time window, duplicate apply
- Election update: timeline validation, blocks core field changes when votes exist
- Review: only status=0 candidates can be reviewed, only status 1 or 2 allowed
