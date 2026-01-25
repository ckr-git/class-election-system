<template>
  <div class="statistics">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalElections || 0 }}</div>
            <div class="stat-label">总选举数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalCandidates || 0 }}</div>
            <div class="stat-label">候选人数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalVotes || 0 }}</div>
            <div class="stat-label">总投票数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <div slot="header">
        <span>选举投票统计</span>
        <el-select v-model="selectedElection" placeholder="选择选举" @change="loadElectionStats"
          style="float: right; width: 200px;">
          <el-option v-for="e in elections" :key="e.id" :label="e.title" :value="e.id" />
        </el-select>
      </div>
      <div id="statisticsChart" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script>
import { getDashboardStats, getElectionVoteStats, getAdminElectionList } from '@/api/admin'

export default {
  name: 'Statistics',
  data() {
    return {
      stats: {},
      elections: [],
      selectedElection: null,
      chart: null
    }
  },
  mounted() {
    this.loadStats()
    this.loadElections()
  },
  methods: {
    async loadStats() {
      const res = await getDashboardStats()
      this.stats = res.data || {}
    },
    async loadElections() {
      const res = await getAdminElectionList()
      this.elections = res.data.records || []
      if (this.elections.length > 0) {
        this.selectedElection = this.elections[0].id
        this.loadElectionStats()
      }
    },
    async loadElectionStats() {
      if (!this.selectedElection) return
      const res = await getElectionVoteStats(this.selectedElection)
      this.initChart(res.data)
    },
    initChart(data) {
      if (this.chart) this.chart.dispose()
      this.chart = this.$echarts.init(document.getElementById('statisticsChart'))
      const candidates = data?.candidates || []
      this.chart.setOption({
        title: { text: '候选人得票统计', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: candidates.map(c => c.nickname || `候选人${c.candidateId}`) },
        yAxis: { type: 'value', name: '票数' },
        series: [{ type: 'bar', data: candidates.map(c => c.voteCount), itemStyle: { color: '#409EFF' } }]
      })
    }
  }
}
</script>

<style scoped>
.stat-item { text-align: center; padding: 10px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409EFF; }
.stat-label { color: #909399; margin-top: 5px; }
</style>
