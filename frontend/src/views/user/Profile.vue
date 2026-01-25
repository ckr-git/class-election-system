<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>个人信息</span></div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="学号/工号">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ userInfo.nickname }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : ''">
                {{ userInfo.role === 'ADMIN' ? '管理员' : '学生' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="手机">{{ userInfo.phone || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '未设置' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>修改密码</span></div>
          <el-form :model="pwdForm" label-width="100px">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="pwdForm.confirmPassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' }
    }
  },
  computed: {
    userInfo() { return this.$store.state.userInfo || {} }
  },
  methods: {
    changePassword() {
      if (this.pwdForm.newPassword !== this.pwdForm.confirmPassword) {
        return this.$message.error('两次密码不一致')
      }
      if (this.pwdForm.newPassword.length < 6) {
        return this.$message.error('密码长度至少6位')
      }
      this.$message.success('密码修改成功，请重新登录')
      this.$store.commit('logout')
      this.$router.push('/login')
    }
  }
}
</script>
