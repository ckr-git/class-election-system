<template>
  <div class="users">
    <el-card>
      <div slot="header">
        <span>用户管理</span>
        <el-button style="float: right;" type="primary" size="small" @click="dialogVisible = true">
          新增用户
        </el-button>
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(scope.row)">
              重置密码
            </el-button>
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
    <el-dialog :visible.sync="dialogVisible" title="用户信息" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item label="密码">
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
  </div>
</template>

<script>
import { getUserList, createUser, updateUser, resetPassword, toggleUserStatus } from '@/api/admin'

export default {
  name: 'Users',
  data() {
    return {
      users: [],
      page: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      form: {
        username: '',
        nickname: '',
        password: '',
        role: 'STUDENT'
      }
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        const res = await getUserList({
          current: this.page.current,
          size: this.page.size
        })
        this.users = res.data.records
        this.page.total = res.data.total
      } catch (error) {
        console.error(error)
      }
    },
    handlePageChange(page) {
      this.page.current = page
      this.loadUsers()
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSave() {
      try {
        if (this.form.id) {
          await updateUser(this.form)
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
        await toggleUserStatus(row.id)
        this.$message.success('操作成功')
        this.loadUsers()
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>
