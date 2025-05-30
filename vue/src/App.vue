<template>
  <el-container class="app-container">
    <el-header>
      <div class="header-content">
        <div class="logo">图书馆管理系统</div>
        <el-menu
          mode="horizontal"
          :router="true"
          :default-active="activeMenu"
        >
          <el-menu-item index="/books">图书管理</el-menu-item>
          <el-menu-item index="/borrow">借阅管理</el-menu-item>
          <el-menu-item index="/seats">座位预约</el-menu-item>
          <el-menu-item index="/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="user-info">
          <el-dropdown>
            <span class="user-dropdown">
              {{ userInfo.username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref({
  username: '测试用户'
})

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 退出登录
const handleLogout = () => {
  // TODO: 调用退出登录API
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px;
  color: var(--el-color-primary);
}

.el-menu {
  flex: 1;
  border-bottom: none;
}

.user-info {
  margin-left: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  
  .el-icon {
    margin-left: 4px;
  }
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>

