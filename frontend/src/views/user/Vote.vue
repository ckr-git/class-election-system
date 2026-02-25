<template>
  <div class="vote">
    <el-card>
      <div slot="header">
        <span>在线投票 - {{ election.title }}</span>
      </div>

      <div v-if="voteInfo" style="margin-bottom: 15px;">
        <el-alert
          :title="`已投 ${voteInfo.votedCount} / 共可投 ${voteInfo.voteLimit} 票`"
          :type="voteInfo.votedCount >= voteInfo.voteLimit ? 'success' : 'info'"
          show-icon :closable="false" />
      </div>

      <el-table :data="candidates" border>
        <el-table-column prop="nickname" label="候选人" width="120" />
        <el-table-column prop="slogan" label="竞选口号" />
        <el-table-column prop="intro" label="个人简介" />
        <el-table-column prop="voteCount" label="当前票数" width="100" />
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              :disabled="hasVoted || votedIds.includes(scope.row.id)"
              @click="handleVote(scope.row)">
              投票
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getElectionDetail } from '@/api/election'
import { getCandidateList } from '@/api/candidate'
import { submitVote, getMyVotes, getVoteCount } from '@/api/vote'

export default {
  name: 'Vote',
  data() {
    return {
      electionId: null,
      election: {},
      candidates: [],
      hasVoted: false,
      votedIds: [],
      voteInfo: null
    }
  },
  mounted() {
    this.electionId = this.$route.query.electionId
    if (this.electionId) {
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      await this.loadElection()
      await this.loadCandidates()
      await this.loadVoteCount()
      await this.checkVoted()
    },
    async loadElection() {
      try {
        const res = await getElectionDetail(this.electionId)
        this.election = res.data
      } catch (e) {
        console.error(e)
      }
    },
    async loadCandidates() {
      try {
        const res = await getCandidateList({ electionId: this.electionId })
        this.candidates = res.data.records
      } catch (e) {
        console.error(e)
      }
    },
    async loadVoteCount() {
      try {
        const res = await getVoteCount({ electionId: this.electionId })
        this.voteInfo = res.data
      } catch (e) {
        console.error(e)
      }
    },
    async checkVoted() {
      try {
        const res = await getMyVotes({ electionId: this.electionId })
        if (res.data && res.data.length > 0) {
          this.votedIds = res.data.map(v => v.candidateId)
          const limit = this.voteInfo ? this.voteInfo.voteLimit : (this.election.voteLimit || 1)
          this.hasVoted = this.votedIds.length >= limit
        }
      } catch (e) {
        console.error(e)
      }
    },
    async handleVote(candidate) {
      try {
        await this.$confirm(`确定要投票给 ${candidate.nickname} 吗？`, '确认投票')
        const res = await submitVote({
          electionId: this.electionId,
          candidateId: candidate.id
        })
        if (res.code === 200) {
          this.$message.success('投票成功')
          await this.loadVoteCount()
          await this.checkVoted()
          await this.loadCandidates()
          // 投满后自动跳转结果页
          if (this.voteInfo && this.voteInfo.votedCount >= this.voteInfo.voteLimit) {
            this.$router.push({ path: '/results', query: { electionId: this.electionId } })
          }
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error('投票失败')
        }
      }
    }
  }
}
</script>
