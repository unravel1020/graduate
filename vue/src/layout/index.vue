<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <router-link to="/">图书馆管理系统</router-link>
      </div>
      <div class="nav">
        <el-menu
          mode="horizontal"
          :router="true"
          :default-active="activeMenu"
        >
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <el-icon><component :is="item.meta.icon" /></el-icon>
            <span>{{ item.meta.title }}</span>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="user-info">
        <el-dropdown @command="handleCommand">
          <span class="user-dropdown">
            {{ userInfo.username }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref({
  username: localStorage.getItem('username') || '未登录'
})

// 菜单项
const menuItems = computed(() => {
  return router.options.routes[0].children.filter(item => item.meta)
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    ElMessage.success('退出成功')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px;
}

.logo a {
  color: var(--primary-color);
  text-decoration: none;
}

.nav {
  flex: 1;
}

.user-info {
  margin-left: 20px;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.main {
  flex: 1;
  background-color: var(--background-color);
  padding: 20px;
}
</style> 