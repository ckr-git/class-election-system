<template>
  <div class="elections">
    <el-card>
      <div slot="header">
        <span>选举管理</span>
        <el-button type="primary" size="small" style="float: right" @click="showCreateDialog">
          创建选举
        </el-button>
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
        <el-table-column prop="startTime" label="投票开始" width="160" />
        <el-table-column prop="endTime" label="投票结束" width="160" />
        <el-table-column label="操作" width="280">
          <template slot-scope="scope">
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
    </el-card>

    <el-dialog title="创建选举" :visible.sync="dialogVisible" width="500px">
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
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminElectionList, createElection, changeElectionStatus, deleteElection } from '@/api/admin'

export default {
  name: 'AdminElections',
  data() {
    return {
      elections: [],
      dialogVisible: false,
      form: {
        title: '',
        description: '',
        applyTime: [],
        voteTime: [],
        voteLimit: 1
      }
    }
  },
  mounted() {
    this.loadElections()
  },
  methods: {
    async loadElections() {
      const res = await getAdminElectionList()
      this.elections = res.data.records
    },
    showCreateDialog() {
      this.form = { title: '', description: '', applyTime: [], voteTime: [], voteLimit: 1 }
      this.dialogVisible = true
    },
    async handleCreate() {
      const data = {
        title: this.form.title,
        description: this.form.description,
        applyStartTime: this.form.applyTime[0],
        applyEndTime: this.form.applyTime[1],
        startTime: this.form.voteTime[0],
        endTime: this.form.voteTime[1],
        voteLimit: this.form.voteLimit,
        status: 0
      }
      await createElection(data)
      this.$message.success('创建成功')
      this.dialogVisible = false
      this.loadElections()
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
    }
  }
}
</script>
