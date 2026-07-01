<template>
  <div class="users">
    <el-card>
      <div slot="header">
        <span>用户管理</span>
        <div style="float: right;">
          <el-button type="success" size="small" @click="importDialogVisible = true">批量导入</el-button>
          <el-button type="primary" size="small" @click="showCreateDialog">新增用户</el-button>
        </div>
      </div>

      <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
        <el-input v-model="searchKeyword" placeholder="搜索学号/姓名" size="small" style="width: 200px;"
          clearable @clear="handleSearch" @keyup.enter.native="handleSearch" />
        <el-select v-model="searchRole" placeholder="角色筛选" size="small" style="width: 120px;" clearable @change="handleSearch">
          <el-option label="学生" value="STUDENT" />
          <el-option label="管理员" value="ADMIN" />
        </el-select>
        <el-button size="small" type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="users" border>
        <el-table-column prop="username" label="学号/工号" width="120" />
        <el-table-column prop="nickname" label="姓名" width="100" />
        <el-table-column prop="role" label="角色" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.role === 'ADMIN'" type="danger">管理员</el-tag>
            <el-tag v-else>学生</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(scope.row)">重置密码</el-button>
            <el-button size="small" :type="scope.row.status === 1 ? 'danger' : 'success'"
                       @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="formRules" ref="userForm" label-width="80px">
        <el-form-item label="学号" prop="username">
          <el-input v-model="form.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit" prop="password">
          <el-input v-model="form.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="学生" value="STUDENT"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入用户" :visible.sync="importDialogVisible" width="500px">
      <div style="margin-bottom: 15px; color: #909399; font-size: 13px;">
        Excel格式：学号、姓名、班级ID、手机号、邮箱。默认密码为学号后6位。
      </div>
      <el-upload
        ref="importUpload"
        action=""
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange">
        <el-button size="small" type="primary">选择文件</el-button>
      </el-upload>
      <div v-if="importResult" style="margin-top: 15px;">
        <el-alert :title="`导入完成：成功 ${importResult.successCount} 条，失败 ${importResult.failCount} 条`"
          :type="importResult.failCount > 0 ? 'warning' : 'success'" show-icon :closable="false" />
        <div v-if="importResult.failDetails && importResult.failDetails.length > 0" style="margin-top: 10px; max-height: 150px; overflow-y: auto;">
          <p v-for="(detail, idx) in importResult.failDetails" :key="idx" style="color: #F56C6C; font-size: 12px; margin: 4px 0;">{{ detail }}</p>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="importDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="importing" @click="handleImport">开始导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserList, createUser, updateUser, resetPassword, toggleUserStatus, importUsers } from '@/api/admin'
import { formatTime } from '@/utils/format'

export default {
  name: 'Users',
  data() {
    return {
      users: [],
      page: { current: 1, size: 10, total: 0 },
      dialogVisible: false,
      isEdit: false,
      searchKeyword: '',
      searchRole: '',
      form: { username: '', nickname: '', password: '', role: 'STUDENT' },
      formRules: {
        username: [{ required: true, message: '请输入学号', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      importDialogVisible: false,
      importFile: null,
      importResult: null,
      importing: false
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        const params = { current: this.page.current, size: this.page.size }
        if (this.searchKeyword) params.username = this.searchKeyword
        if (this.searchRole) params.role = this.searchRole
        const res = await getUserList(params)
        this.users = res.data.records
        this.page.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    handleSearch() {
      this.page.current = 1
      this.loadUsers()
    },
    handlePageChange(page) {
      this.page.current = page
      this.loadUsers()
    },
    showCreateDialog() {
      this.isEdit = false
      this.form = { username: '', nickname: '', password: '', role: 'STUDENT' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = { id: row.id, username: row.username, nickname: row.nickname, role: row.role }
      this.dialogVisible = true
    },
    resetForm() {
      this.form = { username: '', nickname: '', password: '', role: 'STUDENT' }
      this.isEdit = false
    },
    async handleSave() {
      const valid = await this.$refs.userForm.validate().catch(() => false)
      if (!valid) return
      try {
        if (this.isEdit) {
          const data = { id: this.form.id, nickname: this.form.nickname, role: this.form.role }
          await updateUser(data)
        } else {
          await createUser(this.form)
        }
        this.$message.success('保存成功')
        this.dialogVisible = false
        this.loadUsers()
      } catch (error) {
        console.error(error)
      }
    },
    async handleResetPassword(row) {
      this.$prompt('请输入新密码', '重置密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(async ({ value }) => {
        await resetPassword({ userId: row.id, newPassword: value })
        this.$message.success('重置成功')
      })
    },
    async handleToggleStatus(row) {
      try {
        const action = row.status === 1 ? '禁用' : '启用'
        await this.$confirm(`确定${action}该用户吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await toggleUserStatus(row.id)
        this.$message.success('操作成功')
        this.loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error(error)
        }
      }
    },
    handleFileChange(file) {
      this.importFile = file.raw
      this.importResult = null
    },
    async handleImport() {
      if (!this.importFile) {
        this.$message.warning('请先选择文件')
        return
      }
      this.importing = true
      try {
        const res = await importUsers(this.importFile)
        this.importResult = res.data
        this.loadUsers()
      } catch (e) {
        this.$message.error('导入失败')
      } finally {
        this.importing = false
      }
    },
    formatTime
  }
}
</script>
