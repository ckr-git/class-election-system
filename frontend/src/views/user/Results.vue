<template>
  <div class="results">
    <el-card>
      <div slot="header">
        <span>投票结果</span>
      </div>

      <el-select v-model="selectedElection" placeholder="选择选举" @change="loadResults" style="margin-bottom: 20px;">
        <el-option v-for="e in elections" :key="e.id" :label="e.title" :value="e.id" />
      </el-select>

      <div v-if="resultData" class="result-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <div id="chart" style="width: 100%; height: 400px;"></div>
          </el-col>
          <el-col :span="12">
            <el-table :data="resultData.candidates" border>
              <el-table-column prop="nickname" label="候选人" />
              <el-table-column prop="voteCount" label="票数" width="100" />
              <el-table-column label="占比" width="100">
                <template slot-scope="scope">
                  {{ getPercent(scope.row.voteCount) }}%
                </template>
              </el-table-column>
            </el-table>
            <p style="margin-top: 15px; color: #909399;">总投票数: {{ resultData.totalVotes }}</p>
          </el-col>
        </el-row>
      </div>

      <div v-else class="empty-tip">请选择一个选举查看结果</div>
    </el-card>
  </div>
</template>

<script>
import { getElectionList } from '@/api/election'
import { getVoteResult } from '@/api/vote'

export default {
  name: 'Results',
  data() {
    return {
      elections: [],
      selectedElection: null,
      resultData: null,
      chart: null
    }
  },
  mounted() {
    this.loadElections()
  },
  methods: {
    async loadElections() {
      const res = await getElectionList()
      this.elections = res.data.records || []
    },
    async loadResults() {
      if (!this.selectedElection) return
      const res = await getVoteResult(this.selectedElection)
      this.resultData = res.data
      this.$nextTick(() => this.initChart())
    },
    getPercent(count) {
      if (!this.resultData || this.resultData.totalVotes === 0) return 0
      return ((count / this.resultData.totalVotes) * 100).toFixed(1)
    },
    initChart() {
      if (!this.resultData) return
      if (this.chart) this.chart.dispose()
      this.chart = this.$echarts.init(document.getElementById('chart'))
      const data = this.resultData.candidates.map(c => ({
        name: c.nickname || `候选人${c.candidateId}`,
        value: c.voteCount
      }))
      this.chart.setOption({
        title: { text: '投票分布', left: 'center' },
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: '60%',
          data,
          label: { formatter: '{b}: {c}票 ({d}%)' }
        }]
      })
    }
  }
}
</script>

<style scoped>
.empty-tip { text-align: center; padding: 40px; color: #909399; }
</style>
