<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <h2>欢迎使用班级干部评选系统</h2>
          <p>当前用户：{{ userInfo.nickname || userInfo.username }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="info-card">
          <div slot="header">
            <i class="el-icon-s-opportunity"></i>
            <span>进行中的选举</span>
          </div>
          <div class="card-content">
            <h3>{{ ongoingCount }}</h3>
            <el-button type="text" @click="$router.push('/elections')">查看详情</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="info-card">
          <div slot="header">
            <i class="el-icon-user"></i>
            <span>候选人数量</span>
          </div>
          <div class="card-content">
            <h3>{{ candidateCount }}</h3>
            <el-button type="text" @click="$router.push('/candidates')">查看详情</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="info-card">
          <div slot="header">
            <i class="el-icon-document-checked"></i>
            <span>我的投票</span>
          </div>
          <div class="card-content">
            <h3>{{ myVoteCount }}</h3>
            <el-button type="text" @click="$router.push('/vote')">去投票</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getElectionList } from '@/api/election'
import { getCandidateList } from '@/api/candidate'
import { getMyVotes } from '@/api/vote'

export default {
  name: 'Home',
  data() {
    return {
      ongoingCount: 0,
      candidateCount: 0,
      myVoteCount: 0
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.userInfo
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const electionRes = await getElectionList({ status: 2 })
        this.ongoingCount = electionRes.data.total || 0

        const candidateRes = await getCandidateList({})
        this.candidateCount = candidateRes.data.total || 0

        const voteRes = await getMyVotes({})
        this.myVoteCount = voteRes.data.length || 0
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>
.home h2 {
  margin: 0 0 10px 0;
}

.info-card {
  text-align: center;
}

.card-content h3 {
  font-size: 36px;
  margin: 20px 0;
  color: #409EFF;
}
</style>
