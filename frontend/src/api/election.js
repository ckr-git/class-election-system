import request from '@/utils/request'

// 获取选举列表
export function getElectionList(params) {
  return request({
    url: '/election/list',
    method: 'get',
    params
  })
}

// 获取选举详情
export function getElectionDetail(id) {
  return request({
    url: `/election/${id}`,
    method: 'get'
  })
}
