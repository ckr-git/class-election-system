import request from '@/utils/request'

// ========== 用户管理 ==========
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/admin/user/create',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/user/update',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/user/${id}`,
    method: 'delete'
  })
}

export function resetPassword(data) {
  return request({
    url: '/admin/user/reset-password',
    method: 'post',
    data
  })
}

export function toggleUserStatus(id) {
  return request({
    url: `/admin/user/toggle-status/${id}`,
    method: 'post'
  })
}

// ========== 选举管理 ==========
export function getAdminElectionList(params) {
  return request({
    url: '/admin/election/list',
    method: 'get',
    params
  })
}

export function createElection(data) {
  return request({
    url: '/admin/election/create',
    method: 'post',
    data
  })
}

export function updateElection(data) {
  return request({
    url: '/admin/election/update',
    method: 'put',
    data
  })
}

export function deleteElection(id) {
  return request({
    url: `/admin/election/${id}`,
    method: 'delete'
  })
}

export function changeElectionStatus(data) {
  return request({
    url: '/admin/election/change-status',
    method: 'post',
    data
  })
}

// ========== 候选人管理 ==========
export function getAdminCandidateList(params) {
  return request({
    url: '/admin/candidate/list',
    method: 'get',
    params
  })
}

export function reviewCandidate(data) {
  return request({
    url: '/admin/candidate/review',
    method: 'post',
    data
  })
}

export function deleteCandidate(id) {
  return request({
    url: `/admin/candidate/${id}`,
    method: 'delete'
  })
}

// ========== 数据统计 ==========
export function getDashboardStats() {
  return request({
    url: '/admin/statistics/dashboard',
    method: 'get'
  })
}

export function getElectionVoteStats(electionId) {
  return request({
    url: `/admin/statistics/election/${electionId}`,
    method: 'get'
  })
}

// ========== 批量导入 ==========
export function importUsers(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/admin/user/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
