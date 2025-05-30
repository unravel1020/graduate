import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/books'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/books',
    name: 'Books',
    component: () => import('@/views/books/index.vue')
  },
  {
    path: '/borrow',
    name: 'Borrow',
    component: () => import('@/views/borrow/index.vue')
  },
  {
    path: '/seats',
    name: 'Seats',
    component: () => import('@/views/seats/index.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/index.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
