<template>
  <div class="home-container">
    <el-row :gutter="20">
      <!-- 系统概览卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用图书馆管理系统</span>
            </div>
          </template>
          <div class="welcome-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <el-icon><Document /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.bookCount }}</div>
                    <div class="stat-label">馆藏图书</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <el-icon><User /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.userCount }}</div>
                    <div class="stat-label">注册用户</div>
                  </div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <el-icon><Chair /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ stats.seatCount }}</div>
                    <div class="stat-label">可用座位</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能入口 -->
    <el-row :gutter="20" class="feature-row">
      <el-col :span="8">
        <el-card class="feature-card" @click="router.push('/books')">
          <el-icon class="feature-icon"><Document /></el-icon>
          <h3>图书管理</h3>
          <p>浏览、搜索和借阅图书</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="feature-card" @click="router.push('/seats')">
          <el-icon class="feature-icon"><Chair /></el-icon>
          <h3>座位预约</h3>
          <p>查看和预约图书馆座位</p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="feature-card" @click="router.push('/profile')">
          <el-icon class="feature-icon"><User /></el-icon>
          <h3>个人中心</h3>
          <p>查看个人信息和借阅记录</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新公告 -->
    <el-row :gutter="20" class="notice-row">
      <el-col :span="24">
        <el-card class="notice-card">
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="notice in notices"
              :key="notice.id"
              :timestamp="notice.time"
              placement="top"
            >
              <el-card>
                <h4>{{ notice.title }}</h4>
                <p>{{ notice.content }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, User, Chair } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const stats = ref({
  bookCount: 0,
  userCount: 0,
  seatCount: 0
})

// 公告列表
const notices = ref([
  {
    id: 1,
    title: '系统上线通知',
    content: '图书馆管理系统已正式上线，欢迎使用！',
    time: '2024-03-20'
  },
  {
    id: 2,
    title: '开馆时间调整',
    content: '即日起，图书馆开放时间调整为 8:00-22:00',
    time: '2024-03-19'
  }
])

// 获取统计数据
const fetchStats = async () => {
  try {
    // TODO: 调用后端API获取实际数据
    stats.value = {
      bookCount: 1000,
      userCount: 500,
      seatCount: 200
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content {
  padding: 20px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: var(--background-color);
  border-radius: 8px;
}

.stat-item .el-icon {
  font-size: 48px;
  margin-right: 20px;
  color: var(--primary-color);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-color);
}

.stat-label {
  font-size: 14px;
  color: var(--text-color-secondary);
  margin-top: 5px;
}

.feature-row {
  margin-bottom: 20px;
}

.feature-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 48px;
  color: var(--primary-color);
  margin-bottom: 10px;
}

.feature-card h3 {
  margin: 10px 0;
  color: var(--text-color);
}

.feature-card p {
  color: var(--text-color-secondary);
  font-size: 14px;
}

.notice-row {
  margin-bottom: 20px;
}

.notice-card :deep(.el-timeline-item__content) {
  h4 {
    margin: 0;
    color: var(--text-color);
  }
  p {
    margin: 5px 0 0;
    color: var(--text-color-secondary);
  }
}
</style> 