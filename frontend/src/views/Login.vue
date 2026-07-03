<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="title">班级干部评选系统</h2>
      <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入学号/工号"
            prefix-icon="el-icon-user"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          class="login-button"
          @click="handleLogin"
        >
          登录
        </el-button>
        <div class="footer-links">
          <router-link to="/register">注册账号</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            const trimmedForm = {
              username: this.loginForm.username.trim(),
              password: this.loginForm.password.trim()
            }
            const res = await login(trimmedForm)
            this.$store.dispatch('login', res.data)
            this.$message.success('登录成功')
            
            // 根据角色跳转
            if (res.data.userInfo.role === 'ADMIN') {
              this.$router.push('/admin')
            } else {
              this.$router.push('/')
            }
          } catch (error) {
            console.error(error)
            this.$message.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  margin-top: 10px;
}

.footer-links {
  margin-top: 15px;
  text-align: center;
}

.footer-links a {
  color: #409EFF;
  text-decoration: none;
}
</style>
