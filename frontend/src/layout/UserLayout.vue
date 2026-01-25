<template>
  <div class="user-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">班级干部评选系统</div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/elections">选举列表</el-menu-item>
          <el-menu-item index="/candidates">候选人</el-menu-item>
          <el-menu-item index="/vote">投票</el-menu-item>
          <el-menu-item index="/results">投票结果</el-menu-item>
          <el-menu-item index="/my-applications">我的申请</el-menu-item>
        </el-menu>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userInfo.nickname || userInfo.username }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'UserLayout',
  computed: {
    userInfo() {
      return this.$store.state.userInfo
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('logout')
          this.$router.push('/login')
          this.$message.success('已退出登录')
        })
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  background-color: #545c64;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: white;
  margin-right: 40px;
}

.el-menu {
  flex: 1;
  border: none;
}

.user-info {
  color: white;
  margin-left: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: white;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
