<template>
  <div class="candidates">
    <el-card>
      <div slot="header">
        <span>候选人列表</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="6" v-for="candidate in candidates" :key="candidate.id">
          <el-card class="candidate-card">
            <div class="candidate-info">
              <el-avatar :size="80" :src="candidate.avatar" icon="el-icon-user-solid"/>
              <h3>{{ candidate.nickname }}</h3>
              <p>{{ candidate.slogan }}</p>
              <el-button type="primary" size="small" @click="viewDetail(candidate)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
        :current-page="page.current"
        :page-size="page.size"
        :total="page.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 候选人详情对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="候选人详情" width="600px">
      <div v-if="currentCandidate">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ currentCandidate.nickname }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ currentCandidate.username }}</el-descriptions-item>
          <el-descriptions-item label="竞选口号" :span="2">{{ currentCandidate.slogan }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ currentCandidate.intro }}</el-descriptions-item>
          <el-descriptions-item label="主要成就" :span="2">{{ currentCandidate.achievements }}</el-descriptions-item>
          <el-descriptions-item label="得票数">{{ currentCandidate.voteCount }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCandidateList } from '@/api/candidate'

export default {
  name: 'Candidates',
  data() {
    return {
      candidates: [],
      page: {
        current: 1,
        size: 12,
        total: 0
      },
      dialogVisible: false,
      currentCandidate: null
    }
  },
  mounted() {
    this.loadCandidates()
  },
  methods: {
    async loadCandidates() {
      try {
        const res = await getCandidateList({
          current: this.page.current,
          size: this.page.size
        })
        this.candidates = res.data.records
        this.page.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    handlePageChange(page) {
      this.page.current = page
      this.loadCandidates()
    },
    viewDetail(candidate) {
      this.currentCandidate = candidate
      this.dialogVisible = true
    }
  }
}
</script>

<style scoped>
.candidate-card {
  margin-bottom: 20px;
  text-align: center;
}

.candidate-info h3 {
  margin: 10px 0;
}

.candidate-info p {
  color: #666;
  min-height: 40px;
}
</style>
