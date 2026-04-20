#!/bin/bash
# Smoke test for 班级干部评选系统
# Verifies core services are running and responding

BASE_URL="${BASE_URL:-http://localhost:8080}"
FRONTEND_URL="${FRONTEND_URL:-http://localhost:8081}"
PASS=0
FAIL=0

check() {
  local desc="$1" url="$2" expected="$3"
  status=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
  if [ "$status" = "$expected" ]; then
    echo "[PASS] $desc (HTTP $status)"
    PASS=$((PASS + 1))
  else
    echo "[FAIL] $desc (expected $expected, got $status)"
    FAIL=$((FAIL + 1))
  fi
}

echo "=== Smoke Test: Class Election System ==="
echo ""

# Backend health
check "Backend API reachable" "$BASE_URL/api/auth/login" "200"
check "Election list (no auth)" "$BASE_URL/api/election/list" "401"
check "Admin endpoint (no auth)" "$BASE_URL/api/admin/user/list" "401"

# Auth flow
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
  echo "[PASS] Admin login successful"
  PASS=$((PASS + 1))
  check "Admin election list" "$BASE_URL/api/admin/election/list" "200"
  check "Admin user list" "$BASE_URL/api/admin/user/list" "200"
  check "Dashboard stats" "$BASE_URL/api/admin/statistics/dashboard" "200"
else
  echo "[FAIL] Admin login failed"
  FAIL=$((FAIL + 1))
fi

# Frontend
check "Frontend reachable" "$FRONTEND_URL" "200"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
exit $FAIL
