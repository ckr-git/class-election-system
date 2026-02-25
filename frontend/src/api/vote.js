import request from '@/utils/request'

// 提交投票
export function submitVote(data) {
  return request({
    url: '/vote/submit',
    method: 'post',
    data
  })
}

// 获取投票结果
export function getVoteResult(electionId) {
  return request({
    url: `/vote/result/${electionId}`,
    method: 'get'
  })
}

// 获取我的投票记录
export function getMyVotes(params) {
  return request({
    url: '/vote/my',
    method: 'get',
    params
  })
}

// 获取已投票数
export function getVoteCount(params) {
  return request({
    url: '/vote/count',
    method: 'get',
    params
  })
}
