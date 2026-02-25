<template>
  <div class="candidates">
    <el-card>
      <div slot="header">
        <span>候选人管理</span>
      </div>

      <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
        <el-select v-model="filterElectionId" placeholder="按选举筛选" size="small" style="width: 220px;"
          clearable @change="loadCandidates">
          <el-option v-for="e in electionOptions" :key="e.id" :label="e.title" :value="e.id" />
        </el-select>
      </div>

      <el-table :data="candidates" border>
        <el-table-column prop="nickname" label="姓名" width="100" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="slogan" label="竞选口号" show-overflow-tooltip />
        <el-table-column prop="voteCount" label="票数" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewOpinion" label="审核意见" show-overflow-tooltip>
          <template slot-scope="scope">
            <span v-if="scope.row.reviewOpinion">{{ scope.row.reviewOpinion }}</span>
            <span v-else style="color: #C0C4CC;">暂无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template slot-scope="scope">
            <template v-if="scope.row.status === 0">
              <el-button size="mini" type="success" @click="handleApprove(scope.row)">通过</el-button>
              <el-button size="mini" type="danger" @click="showRejectDialog(scope.row)">拒绝</el-button>
            </template>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 拒绝原因对话框 -->
    <el-dialog title="拒绝原因" :visible.sync="rejectDialogVisible" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <div slot="footer">
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReject">确定拒绝</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminCandidateList, getAdminElectionList, reviewCandidate, deleteCandidate } from '@/api/admin'

export default {
  name: 'AdminCandidates',
  data() {
    return {
      candidates: [],
      electionOptions: [],
      filterElectionId: null,
      rejectDialogVisible: false,
      rejectReason: '',
      rejectRow: null
    }
  },
  mounted() {
    this.loadElections()
    this.loadCandidates()
  },
  methods: {
    async loadElections() {
      try {
        const res = await getAdminElectionList({ size: 100 })
        this.electionOptions = res.data.records || []
      } catch (e) {
        console.error(e)
      }
    },
    async loadCandidates() {
      const params = {}
      if (this.filterElectionId) params.electionId = this.filterElectionId
      const res = await getAdminCandidateList(params)
      this.candidates = res.data.records
    },
    async handleApprove(row) {
      await this.$confirm('确定通过该候选人?', '提示')
      await reviewCandidate({ candidateId: row.id, status: 1, reviewOpinion: '审核通过' })
      this.$message.success('审核成功')
      this.loadCandidates()
    },
    showRejectDialog(row) {
      this.rejectRow = row
      this.rejectReason = ''
      this.rejectDialogVisible = true
    },
    async handleReject() {
      if (!this.rejectReason.trim()) {
        this.$message.warning('请输入拒绝原因')
        return
      }
      await reviewCandidate({ candidateId: this.rejectRow.id, status: 2, reviewOpinion: this.rejectReason })
      this.$message.success('已拒绝')
      this.rejectDialogVisible = false
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
