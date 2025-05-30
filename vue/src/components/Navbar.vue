<template>
  <el-menu
    :default-active="activeIndex"
    class="navbar"
    mode="horizontal"
    router
  >
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/books">图书列表</el-menu-item>
    <el-menu-item index="/borrow-records">我的借阅</el-menu-item>
    <el-menu-item index="/seats">座位预约</el-menu-item>
    <el-menu-item index="/model">3D模型</el-menu-item>
    <div class="flex-grow" />
    <template v-if="isLoggedIn">
      <el-menu-item index="/profile">个人中心</el-menu-item>
      <el-menu-item @click="handleLogout">退出登录</el-menu-item>
    </template>
    <template v-else>
      <el-menu-item index="/login">登录</el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activeIndex = computed(() => route.path)

const isLoggedIn = computed(() => {
  return localStorage.getItem('token') !== null
})

const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  padding: 0 20px;
}

.flex-grow {
  flex-grow: 1;
}
</style> 