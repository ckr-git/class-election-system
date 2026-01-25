<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">管理后台</div>
        <el-menu
          :default-active="activeMenu"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-s-home"></i>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/elections">
            <i class="el-icon-s-order"></i>
            <span>选举管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/candidates">
            <i class="el-icon-s-custom"></i>
            <span>候选人管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <i class="el-icon-s-data"></i>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <span>班级干部评选系统 - 管理后台</span>
          <div class="user-info">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ userInfo.nickname || userInfo.username }}
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main-content">
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'AdminLayout',
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
      }
    }
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.el-aside {
  background-color: #545c64;
  min-height: 100vh;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: white;
  background-color: #444;
}

.el-menu {
  border: none;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.user-info {
  margin-left: auto;
}

.el-dropdown-link {
  cursor: pointer;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
