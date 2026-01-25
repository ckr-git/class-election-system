<template>
  <div class="vote">
    <el-card>
      <div slot="header">
        <span>在线投票 - {{ election.title }}</span>
      </div>

      <el-alert v-if="hasVoted" title="您已完成投票" type="success" show-icon :closable="false" style="margin-bottom: 20px;" />

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
import { submitVote, getMyVotes } from '@/api/vote'

export default {
  name: 'Vote',
  data() {
    return {
      electionId: null,
      election: {},
      candidates: [],
      hasVoted: false,
      votedIds: []
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
    async checkVoted() {
      try {
        const res = await getMyVotes({ electionId: this.electionId })
        if (res.data && res.data.length > 0) {
          this.votedIds = res.data.map(v => v.candidateId)
          this.hasVoted = this.votedIds.length >= (this.election.voteLimit || 1)
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
          this.loadData()
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
