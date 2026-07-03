<!-- Generated: 2026-03-22 | Files scanned: 22 | Token estimate: ~500 -->

# Frontend

## Route Tree
```
/login                    → Login.vue          (public)
/register                 → Register.vue       (public)

/ (UserLayout)                                  (requireAuth)
  /home                   → Home.vue
  /elections              → Elections.vue       (list + apply dialog)
  /candidates             → Candidates.vue
  /vote                   → Vote.vue           (vote + count display)
  /results                → Results.vue        (ECharts pie/bar)
  /my-applications        → MyApplications.vue
  /profile                → Profile.vue

/admin (AdminLayout)                            (requireAuth + requireAdmin)
  /admin/dashboard        → Dashboard.vue      (ECharts stats)
  /admin/users            → Users.vue          (CRUD + import)
  /admin/elections        → Elections.vue       (CRUD + status change)
  /admin/candidates       → Candidates.vue     (review + filter)
  /admin/statistics       → Statistics.vue     (ECharts per-election)
```

## API Layer (frontend/src/api/)
```
auth.js       → register, login
election.js   → getElectionList, getElectionDetail
candidate.js  → applyCandidate, getCandidateList
vote.js       → submitVote, getVoteResult, getMyVotes, getVoteCount
admin.js      → getUserList, createUser, updateUser, resetPassword,
                toggleUserStatus, importUsers, getAdminElectionList,
                createElection, updateElection, deleteElection,
                changeElectionStatus, getAdminCandidateList,
                reviewCandidate, deleteCandidate, getDashboardStats,
                getElectionVoteStats
```

## Vuex Store
```
state:     { token: String, userInfo: Object }
mutations: SET_TOKEN, SET_USER_INFO, LOGOUT
actions:   login (API call + commit), logout (clear state + cookie)
persist:   token in localStorage + Cookies
```

## Shared Utils
```
utils/request.js  → Axios instance, baseURL=/api, auth header injection, 401 auto-logout
utils/format.js   → formatTime(isoString) → "YYYY-MM-DD HH:mm"
```

## Route Guards
- beforeEach: check token → redirect to /login if missing
- requireAdmin: check userInfo.role === 'ADMIN' → redirect to / if not
