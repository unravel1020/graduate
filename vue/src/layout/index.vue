<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container">
      <div class="logo">
        <el-icon class="logo-icon"><Reading /></el-icon>
        <span>座位预约系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        
        <el-menu-item index="/seats">
          <el-icon><Position /></el-icon>
          <span>座位预约</span>
        </el-menu-item>
        
        <el-menu-item index="/books">
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        
        <el-menu-item index="/borrows">
          <el-icon><Document /></el-icon>
          <span>借阅管理</span>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
        
        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><UserFilled /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主要内容区 -->
    <div class="main-container">
      <!-- 顶部导航栏 -->
      <div class="navbar">
        <div class="left">
          <el-icon class="fold-btn" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        <div class="right">
          <el-dropdown trigger="click">
            <div class="avatar-container">
              <el-avatar :size="32" :src="userInfo?.avatar || defaultAvatar" />
              <span class="username">{{ userInfo?.username || '未登录' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  House,
  Position,
  User,
  UserFilled,
  Fold,
  Expand,
  SwitchButton,
  Reading,
  Document
} from '@element-plus/icons-vue'
import { auth } from '@/utils/api'
import Breadcrumb from './components/Breadcrumb.vue'

const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapse = ref(false)
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 用户信息
const userInfo = ref({
  username: '',
  role: '',
  avatar: ''
})
const isAdmin = computed(() => userInfo.value?.role === 'admin')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 获取当前激活的菜单
const activeMenu = computed(() => route.path)

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await auth.getProfile()
    if (res.data) {
      userInfo.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 跳转到个人中心
const handleProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      router.push('/login')
    } catch (error) {
      console.error('退出登录失败:', error)
    }
  })
}

// 初始化
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar-container.collapse {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.logo-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #409EFF;
}

.sidebar-menu {
  border: none;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left {
  display: flex;
  align-items: center;
}

.fold-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.right {
  display: flex;
  align-items: center;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 路由切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 