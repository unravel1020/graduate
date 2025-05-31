<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总座位数</span>
              <el-icon><Position /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalSeats }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>空闲座位</span>
              <el-icon><CircleCheck /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.availableSeats }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已预约</span>
              <el-icon><Timer /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.reservedSeats }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>使用中</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.inUseSeats }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 我的预约 -->
    <el-card class="my-reservations">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
          <el-button type="primary" @click="handleGoToSeats">去预约</el-button>
        </div>
      </template>
      <el-table :data="myReservations" style="width: 100%" v-loading="loading">
        <el-table-column prop="seatNumber" label="座位号" />
        <el-table-column prop="area" label="区域" />
        <el-table-column prop="startTime" label="开始时间">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长">
          <template #default="{ row }">
            {{ row.duration }}小时
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'confirmed'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 系统公告 -->
    <el-card class="announcements">
      <template #header>
        <div class="card-header">
          <span>系统公告</span>
        </div>
      </template>
      <div class="announcement-list">
        <div v-for="item in announcements" :key="item.id" class="announcement-item">
          <div class="announcement-title">
            <el-icon><Bell /></el-icon>
            <span>{{ item.title }}</span>
          </div>
          <div class="announcement-content">{{ item.content }}</div>
          <div class="announcement-time">{{ formatDate(item.createTime) }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Position,
  CircleCheck,
  Timer,
  User,
  Bell
} from '@element-plus/icons-vue'
import { seats } from '@/utils/api'

const router = useRouter()
const loading = ref(false)

// 统计数据
const stats = reactive({
  totalSeats: 0,
  availableSeats: 0,
  reservedSeats: 0,
  inUseSeats: 0
})

// 我的预约
const myReservations = ref([])

// 系统公告
const announcements = ref([
  {
    id: 1,
    title: '系统上线通知',
    content: '座位预约系统已正式上线，欢迎使用！',
    createTime: '2024-03-20 10:00:00'
  },
  {
    id: 2,
    title: '使用说明',
    content: '请在使用座位时保持安静，爱护公共设施。',
    createTime: '2024-03-20 09:00:00'
  }
])

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await seats.getStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取我的预约
const fetchMyReservations = async () => {
  try {
    loading.value = true
    const res = await seats.getMyReservations()
    myReservations.value = res.data
  } catch (error) {
    console.error('获取预约记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 取消预约
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await seats.cancelReservation(row.id)
      ElMessage.success('取消成功')
      fetchMyReservations()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

// 跳转到座位预约页面
const handleGoToSeats = () => {
  router.push('/seats')
}

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'confirmed':
      return 'success'
    case 'cancelled':
      return 'danger'
    case 'completed':
      return 'info'
    default:
      return 'warning'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'confirmed':
      return '已确认'
    case 'cancelled':
      return '已取消'
    case 'completed':
      return '已完成'
    default:
      return '待确认'
  }
}

// 初始化
onMounted(() => {
  fetchStats()
  fetchMyReservations()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 20px 0;
}

.my-reservations,
.announcements {
  margin-bottom: 20px;
}

.announcement-list {
  .announcement-item {
    padding: 15px 0;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }

  .announcement-title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;

    .el-icon {
      margin-right: 8px;
      color: #409EFF;
    }
  }

  .announcement-content {
    color: #666;
    margin-bottom: 10px;
  }

  .announcement-time {
    color: #999;
    font-size: 12px;
  }
}
</style> 