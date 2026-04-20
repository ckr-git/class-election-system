#!/bin/bash
# API integration tests for 班级干部评选系统
BASE="${BASE_URL:-http://localhost:8080}"
PASS=0
FAIL=0
SKIP=0

assert_code() {
  local desc="$1" actual="$2" expected="$3"
  if [ "$actual" = "$expected" ]; then
    echo "[PASS] $desc"
    PASS=$((PASS + 1))
  else
    echo "[FAIL] $desc (expected $expected, got $actual)"
    FAIL=$((FAIL + 1))
  fi
}

# Get admin token
ADMIN_TOKEN=$(curl -s -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Auth endpoints
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}')
assert_code "POST /api/auth/login (valid)" "$CODE" "200"

CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/auth/login" \
  -H "Content-Type: application/json" -d '{"username":"admin","password":"wrong"}')
assert_code "POST /api/auth/login (wrong password)" "$CODE" "200"

# Protected endpoints without auth
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/election/list")
assert_code "GET /api/election/list (no auth) -> 401" "$CODE" "401"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/user/list")
assert_code "GET /api/admin/user/list (no auth) -> 401" "$CODE" "401"

# Admin endpoints with auth
CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/election/list" \
  -H "Authorization: Bearer $ADMIN_TOKEN")
assert_code "GET /api/admin/election/list" "$CODE" "200"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/user/list" \
  -H "Authorization: Bearer $ADMIN_TOKEN")
assert_code "GET /api/admin/user/list" "$CODE" "200"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/statistics/dashboard" \
  -H "Authorization: Bearer $ADMIN_TOKEN")
assert_code "GET /api/admin/statistics/dashboard" "$CODE" "200"

CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/api/admin/candidate/list" \
  -H "Authorization: Bearer $ADMIN_TOKEN")
assert_code "GET /api/admin/candidate/list" "$CODE" "200"

echo ""
echo "=== API Tests: $PASS passed, $FAIL failed, $SKIP skipped ==="
exit $FAIL
