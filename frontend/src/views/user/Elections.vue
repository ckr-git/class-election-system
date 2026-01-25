<template>
  <div class="elections">
    <el-card>
      <div slot="header">
        <span>选举列表</span>
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
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 1" size="small" @click="applyElection(scope.row)">
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

      <el-pagination
        :current-page="page.current"
        :page-size="page.size"
        :total="page.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 20px;"
      />
    </el-card>
  </div>
</template>

<script>
import { getElectionList } from '@/api/election'

export default {
  name: 'Elections',
  data() {
    return {
      elections: [],
      page: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    this.loadElections()
  },
  methods: {
    async loadElections() {
      try {
        const res = await getElectionList({
          current: this.page.current,
          size: this.page.size
        })
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
    applyElection(election) {
      this.$router.push(`/candidates?electionId=${election.id}`)
    },
    voteElection(election) {
      this.$router.push(`/vote?electionId=${election.id}`)
    },
    viewResult(election) {
      this.$router.push(`/results?electionId=${election.id}`)
    }
  }
}
</script>
