<template>
  <div class="elections">
    <el-card>
      <div slot="header">
        <span>选举管理</span>
        <el-button type="primary" size="small" style="float: right" @click="showCreateDialog">
          创建选举
        </el-button>
      </div>

      <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
        <el-input v-model="searchKeyword" placeholder="搜索选举标题" size="small" style="width: 220px;"
          clearable @clear="handleSearch" @keyup.enter.native="handleSearch" />
        <el-button size="small" type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="elections" border>
        <el-table-column prop="title" label="选举标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0">未开始</el-tag>
            <el-tag v-if="scope.row.status === 1" type="warning">报名中</el-tag>
            <el-tag v-if="scope.row.status === 2" type="success">投票中</el-tag>
            <el-tag v-if="scope.row.status === 3" type="info">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="投票开始" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="投票结束" width="160">
          <template slot-scope="scope">{{ formatTime(scope.row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" @click="changeStatus(scope.row, 1)" v-if="scope.row.status === 0">
              开始报名
            </el-button>
            <el-button size="mini" type="success" @click="changeStatus(scope.row, 2)" v-if="scope.row.status === 1">
              开始投票
            </el-button>
            <el-button size="mini" type="info" @click="changeStatus(scope.row, 3)" v-if="scope.row.status === 2">
              结束投票
            </el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :current-page="page.current"
        :page-size="page.size"
        :total="page.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog :title="isEdit ? '编辑选举' : '创建选举'" :visible.sync="dialogVisible" width="500px"
      @close="resetForm">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选举标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="选举描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="报名时间">
          <el-date-picker v-model="form.applyTime" type="datetimerange"
            start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="投票时间">
          <el-date-picker v-model="form.voteTime" type="datetimerange"
            start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="投票限制">
          <el-input-number v-model="form.voteLimit" :min="1" :max="10" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminElectionList, createElection, updateElection, changeElectionStatus, deleteElection } from '@/api/admin'

export default {
  name: 'AdminElections',
  data() {
    return {
      elections: [],
      searchKeyword: '',
      page: { current: 1, size: 10, total: 0 },
      dialogVisible: false,
      isEdit: false,
      editId: null,
      form: { title: '', description: '', applyTime: [], voteTime: [], voteLimit: 1 }
    }
  },
  mounted() {
    this.loadElections()
  },
  methods: {
    async loadElections() {
      const params = { current: this.page.current, size: this.page.size }
      if (this.searchKeyword) params.keyword = this.searchKeyword
      const res = await getAdminElectionList(params)
      this.elections = res.data.records
      this.page.total = res.data.total
    },
    handleSearch() {
      this.page.current = 1
      this.loadElections()
    },
    handlePageChange(page) {
      this.page.current = page
      this.loadElections()
    },
    showCreateDialog() {
      this.isEdit = false
      this.editId = null
      this.form = { title: '', description: '', applyTime: [], voteTime: [], voteLimit: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.editId = row.id
      this.form = {
        title: row.title,
        description: row.description,
        applyTime: row.applyStartTime && row.applyEndTime ? [row.applyStartTime, row.applyEndTime] : [],
        voteTime: row.startTime && row.endTime ? [row.startTime, row.endTime] : [],
        voteLimit: row.voteLimit || 1
      }
      this.dialogVisible = true
    },
    async handleSave() {
      const data = {
        title: this.form.title,
        description: this.form.description,
        applyStartTime: this.form.applyTime && this.form.applyTime[0],
        applyEndTime: this.form.applyTime && this.form.applyTime[1],
        startTime: this.form.voteTime && this.form.voteTime[0],
        endTime: this.form.voteTime && this.form.voteTime[1],
        voteLimit: this.form.voteLimit
      }
      try {
        if (this.isEdit) {
          data.id = this.editId
          await updateElection(data)
          this.$message.success('更新成功')
        } else {
          data.status = 0
          await createElection(data)
          this.$message.success('创建成功')
        }
        this.dialogVisible = false
        this.loadElections()
      } catch (e) {
        console.error(e)
      }
    },
    resetForm() {
      this.form = { title: '', description: '', applyTime: [], voteTime: [], voteLimit: 1 }
      this.isEdit = false
      this.editId = null
    },
    async changeStatus(row, status) {
      await changeElectionStatus({ electionId: row.id, status })
      this.$message.success('状态更新成功')
      this.loadElections()
    },
    async handleDelete(row) {
      await this.$confirm('确定删除该选举?', '提示')
      await deleteElection(row.id)
      this.$message.success('删除成功')
      this.loadElections()
    },
    formatTime(time) {
      if (!time) return ''
      return time.replace('T', ' ').substring(0, 16)
    }
  }
}
</script>
