import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/UserLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页', requireAuth: true }
      },
      {
        path: 'elections',
        name: 'Elections',
        component: () => import('@/views/user/Elections.vue'),
        meta: { title: '选举列表', requireAuth: true }
      },
      {
        path: 'candidates',
        name: 'Candidates',
        component: () => import('@/views/user/Candidates.vue'),
        meta: { title: '候选人', requireAuth: true }
      },
      {
        path: 'vote',
        name: 'Vote',
        component: () => import('@/views/user/Vote.vue'),
        meta: { title: '投票', requireAuth: true }
      },
      {
        path: 'results',
        name: 'Results',
        component: () => import('@/views/user/Results.vue'),
        meta: { title: '投票结果', requireAuth: true }
      },
      {
        path: 'my-applications',
        name: 'MyApplications',
        component: () => import('@/views/user/MyApplications.vue'),
        meta: { title: '我的申请', requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'elections',
        name: 'AdminElections',
        component: () => import('@/views/admin/Elections.vue'),
        meta: { title: '选举管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'candidates',
        name: 'AdminCandidates',
        component: () => import('@/views/admin/Candidates.vue'),
        meta: { title: '候选人管理', requireAuth: true, requireAdmin: true }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计', requireAuth: true, requireAdmin: true }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 班级干部评选系统` : '班级干部评选系统'
  
  const token = store.state.token
  const userInfo = store.state.userInfo

  if (to.meta.requireAuth) {
    if (!token) {
      next('/login')
    } else if (to.meta.requireAdmin && userInfo.role !== 'ADMIN') {
      next('/')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
