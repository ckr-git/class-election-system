<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-user stat-icon"></i>
            <div>
              <h4>总用户数</h4>
              <p class="stat-number">{{ stats.totalUsers || 0 }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-s-order stat-icon"></i>
            <div>
              <h4>总选举数</h4>
              <p class="stat-number">{{ stats.totalElections || 0 }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-s-custom stat-icon"></i>
            <div>
              <h4>候选人数</h4>
              <p class="stat-number">{{ stats.totalCandidates || 0 }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <i class="el-icon-tickets stat-icon"></i>
            <div>
              <h4>总投票数</h4>
              <p class="stat-number">{{ stats.totalVotes || 0 }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <div slot="header">投票统计</div>
          <div id="voteChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">用户分布</div>
          <div id="userChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboardStats } from '@/api/admin'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {}
    }
  },
  mounted() {
    this.loadStats()
  },
  methods: {
    async loadStats() {
      try {
        const res = await getDashboardStats()
        this.stats = res.data
        this.$nextTick(() => {
          this.initCharts()
        })
      } catch (error) {
        console.error(error)
      }
    },
    initCharts() {
      // 投票统计图表
      const voteChart = this.$echarts.init(document.getElementById('voteChart'))
      voteChart.setOption({
        tooltip: {},
        legend: { data: ['投票数'] },
        xAxis: { data: ['班长', '副班长', '学习委员', '生活委员'] },
        yAxis: {},
        series: [{
          name: '投票数',
          type: 'bar',
          data: [120, 95, 80, 65],
          itemStyle: { color: '#409EFF' }
        }]
      })

      // 用户分布图表
      const userChart = this.$echarts.init(document.getElementById('userChart'))
      userChart.setOption({
        tooltip: {},
        series: [{
          name: '用户分布',
          type: 'pie',
          radius: '60%',
          data: [
            { value: this.stats.studentCount, name: '学生' },
            { value: this.stats.totalUsers - this.stats.studentCount, name: '管理员' }
          ]
        }]
      })
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon {
  font-size: 48px;
  color: #409EFF;
  margin-right: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0 0 0;
}

h4 {
  margin: 0;
  color: #666;
}
</style>
