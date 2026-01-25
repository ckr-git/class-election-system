import request from '@/utils/request'

// 申请成为候选人
export function applyCandidate(data) {
  return request({
    url: '/candidate/apply',
    method: 'post',
    data
  })
}

// 获取候选人列表
export function getCandidateList(params) {
  return request({
    url: '/candidate/list',
    method: 'get',
    params
  })
}

// 获取候选人详情
export function getCandidateDetail(id) {
  return request({
    url: `/candidate/${id}`,
    method: 'get'
  })
}
