<template>
  <div class="elections">
    <el-card>
      <div slot="header">
        <span>选举列表</span>
      </div>

      <el-table :data="elections" border v-if="elections.length > 0">
        <el-table-column prop="title" label="选举标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0">未开始</el-tag>
            <el-tag v-if="scope.row.status === 1" type="warning">报名中</el-tag>
            <el-tag v-if="scope.row.status === 2" type="success">投票中</el-tag>
            <el-tag v-if="scope.row.status === 3" type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template slot-scope="scope">{{ formatTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template slot-scope="scope">{{ formatTime(scope.row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 1" size="small" @click="showApplyDialog(scope.row)">
              报名
            </el-button>
            <el-button v-if="scope.row.status === 2" type="primary" size="small" @click="voteElection(scope.row)">
              投票
            </el-button>
            <el-button v-if="scope.row.status === 3" type="success" size="small" @click="viewResult(scope.row)">
              查看结果
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else description="暂无选举活动，请等待管理员创建" />

      <el-pagination
        v-if="elections.length > 0"
        :current-page="page.current"
        :page-size="page.size"
        :total="page.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 报名申请对话框 -->
    <el-dialog title="候选人报名" :visible.sync="applyDialogVisible" width="500px" @close="resetApplyForm">
      <el-form :model="applyForm" label-width="80px" :rules="applyRules" ref="applyFormRef">
        <el-form-item label="选举" v-if="currentElection">
          <span>{{ currentElection.title }}</span>
        </el-form-item>
        <el-form-item label="竞选口号" prop="slogan">
          <el-input v-model="applyForm.slogan" maxlength="500" show-word-limit placeholder="请输入竞选口号" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="applyForm.intro" type="textarea" :rows="3" placeholder="请输入个人简介" />
        </el-form-item>
        <el-form-item label="主要成就">
          <el-input v-model="applyForm.achievements" type="textarea" :rows="3" placeholder="请输入主要成就" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applyLoading" @click="handleApply">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getElectionList } from '@/api/election'
import { applyCandidate } from '@/api/candidate'

export default {
  name: 'Elections',
  data() {
    return {
      elections: [],
      page: { current: 1, size: 10, total: 0 },
      applyDialogVisible: false,
      applyLoading: false,
      currentElection: null,
      applyForm: { slogan: '', intro: '', achievements: '' },
      applyRules: {
        slogan: [{ required: true, message: '请输入竞选口号', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadElections()
  },
  methods: {
    async loadElections() {
      try {
        const res = await getElectionList({ current: this.page.current, size: this.page.size })
        this.elections = res.data.records
        this.page.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    handlePageChange(page) {
      this.page.current = page
      this.loadElections()
    },
    showApplyDialog(election) {
      this.currentElection = election
      this.applyForm = { slogan: '', intro: '', achievements: '' }
      this.applyDialogVisible = true
    },
    resetApplyForm() {
      this.applyForm = { slogan: '', intro: '', achievements: '' }
      this.currentElection = null
    },
    async handleApply() {
      this.$refs.applyFormRef.validate(async (valid) => {
        if (!valid) return
        this.applyLoading = true
        try {
          await applyCandidate({
            electionId: this.currentElection.id,
            slogan: this.applyForm.slogan,
            intro: this.applyForm.intro,
            achievements: this.applyForm.achievements
          })
          this.$message.success('申请成功，等待审核')
          this.applyDialogVisible = false
          this.loadElections()
        } catch (e) {
          this.$message.error('申请失败，请检查是否在报名时间内或已申请过')
        } finally {
          this.applyLoading = false
        }
      })
    },
    voteElection(election) {
      this.$router.push(`/vote?electionId=${election.id}`)
    },
    viewResult(election) {
      this.$router.push(`/results?electionId=${election.id}`)
    },
    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>
