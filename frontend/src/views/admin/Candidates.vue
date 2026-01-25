<template>
  <div class="candidates">
    <el-card>
      <div slot="header">
        <span>候选人管理</span>
      </div>
      <el-table :data="candidates" border>
        <el-table-column prop="nickname" label="姓名" width="100" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="slogan" label="竞选口号" />
        <el-table-column prop="voteCount" label="票数" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="mini" type="success" @click="handleReview(scope.row, 1)">通过</el-button>
              <el-button size="mini" type="danger" @click="handleReview(scope.row, 2)">拒绝</el-button>
            </template>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getAdminCandidateList, reviewCandidate, deleteCandidate } from '@/api/admin'

export default {
  name: 'AdminCandidates',
  data() {
    return { candidates: [] }
  },
  mounted() {
    this.loadCandidates()
  },
  methods: {
    async loadCandidates() {
      const res = await getAdminCandidateList()
      this.candidates = res.data.records
    },
    async handleReview(row, status) {
      const action = status === 1 ? '通过' : '拒绝'
      await this.$confirm(`确定${action}该候选人?`, '提示')
      await reviewCandidate({ candidateId: row.id, status, reviewOpinion: action })
      this.$message.success('审核成功')
      this.loadCandidates()
    },
    async handleDelete(row) {
      await this.$confirm('确定删除该候选人?', '提示')
      await deleteCandidate(row.id)
      this.$message.success('删除成功')
      this.loadCandidates()
    }
  }
}
</script>
