<template>
  <div class="my-applications">
    <el-card>
      <div slot="header">
        <span>我的申请记录</span>
      </div>

      <el-table :data="applications" border v-loading="loading">
        <el-table-column prop="electionTitle" label="选举名称" />
        <el-table-column prop="slogan" label="竞选口号" />
        <el-table-column prop="voteCount" label="当前票数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewOpinion" label="审核意见" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.reviewOpinion">{{ scope.row.reviewOpinion }}</span>
            <span v-else style="color: #C0C4CC;">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
      </el-table>

      <div v-if="applications.length === 0 && !loading" class="empty-tip">
        <p>暂无申请记录</p>
        <el-button type="primary" @click="$router.push('/elections')">去报名</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getCandidateList } from '@/api/candidate'

export default {
  name: 'MyApplications',
  data() {
    return {
      applications: [],
      loading: false
    }
  },
  mounted() {
    this.loadApplications()
  },
  methods: {
    async loadApplications() {
      this.loading = true
      try {
        const userId = this.$store.state.userInfo.id
        const res = await getCandidateList({ userId })
        this.applications = res.data.records || []
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.empty-tip {
  text-align: center;
  padding: 40px;
  color: #909399;
}
</style>
