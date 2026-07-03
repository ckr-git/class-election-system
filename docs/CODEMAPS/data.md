<!-- Generated: 2026-03-22 | Files scanned: 9 | Token estimate: ~500 -->

# Data Model

## Entity Relationship
```
User 1──N Candidate (userId)
User 1──N VoteRecord (voterId)
User 1──N Application (userId)
Election 1──N Candidate (electionId)
Election 1──N VoteRecord (electionId)
Election 1──N Application (electionId)
Position 1──N Candidate (positionId)
Position 1──N Application (positionId)
Candidate 1──N VoteRecord (candidateId)
```

## Tables

### user
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| username | String | unique, 学号 |
| password | String | BCrypt, @JsonIgnore |
| nickname | String | |
| avatar | String | URL |
| classId | Long | |
| phone | String | |
| email | String | |
| role | String | STUDENT / ADMIN |
| status | Integer | 1=active, 0=disabled |
| deleted | Integer | logical delete |
| createTime | LocalDateTime | auto-fill |
| updateTime | LocalDateTime | auto-fill |

### election
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| title | String | XSS filtered |
| description | String | XSS filtered |
| classId | Long | |
| applyStartTime | LocalDateTime | |
| applyEndTime | LocalDateTime | |
| startTime | LocalDateTime | vote start |
| endTime | LocalDateTime | vote end |
| status | Integer | 0=draft, 1=applying, 2=voting, 3=ended |
| voteLimit | Integer | max votes per user |
| creatorId | Long | FK user.id |
| deleted/createTime/updateTime | | standard |

### candidate
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| electionId | Long | FK election.id |
| positionId | Long | FK position.id |
| userId | Long | FK user.id |
| slogan | String | XSS filtered |
| intro | String | XSS filtered |
| achievements | String | XSS filtered |
| photo | String | URL |
| voteCount | Integer | atomic increment |
| status | Integer | 0=pending, 1=approved, 2=rejected |
| reviewOpinion | String | XSS filtered |
| deleted/createTime/updateTime | | standard |

### vote_record
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| electionId | Long | FK election.id |
| voterId | Long | FK user.id |
| candidateId | Long | FK candidate.id |
| ipAddress | String | |
| createTime | LocalDateTime | auto-fill |

### position
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| name | String | |
| description | String | |
| responsibilities | String | |
| maxCount | Integer | |
| sortOrder | Integer | |
| deleted/createTime/updateTime | | standard |

### application
| Column | Type | Notes |
|--------|------|-------|
| id | Long PK AUTO | |
| electionId | Long | FK election.id |
| positionId | Long | FK position.id |
| userId | Long | FK user.id |
| reason | String | |
| status | Integer | 0=pending, 1=approved, 2=rejected |
| reviewOpinion | String | |
| reviewerId | Long | FK user.id |
| reviewTime | LocalDateTime | |
| deleted/createTime/updateTime | | standard |

## Status Machines
```
Election: 0(draft) → 1(applying) → 2(voting) → 3(ended)
Candidate: 0(pending) → 1(approved) | 2(rejected)
Application: 0(pending) → 1(approved) | 2(rejected)
User: 1(active) ↔ 0(disabled)
```
