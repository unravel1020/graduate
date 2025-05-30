<template>
  <div class="seats-container">
    <!-- 日期选择 -->
    <el-card class="date-card">
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        :disabled-date="disabledDate"
        @change="handleDateChange"
      />
    </el-card>

    <!-- 座位状态统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>总座位数</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalSeats }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已预约</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.reservedSeats }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>可用座位</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.availableSeats }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 座位列表 -->
    <el-card class="seats-card">
      <template #header>
        <div class="card-header">
          <span>座位列表</span>
          <div class="header-actions">
            <el-select v-model="selectedArea" placeholder="选择区域" @change="handleAreaChange">
              <el-option
                v-for="area in areas"
                :key="area"
                :label="area"
                :value="area"
              />
            </el-select>
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="grid">网格视图</el-radio-button>
              <el-radio-button label="list">列表视图</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="seats-grid">
        <el-card
          v-for="seat in seats"
          :key="seat.id"
          class="seat-card"
          :class="{ 'is-reserved': seat.status === 1 }"
          @click="handleSeatClick(seat)"
        >
          <div class="seat-info">
            <div class="seat-number">{{ seat.seatNumber }}</div>
            <div class="seat-status">
              <el-tag :type="getStatusType(seat.status)">
                {{ getStatusText(seat.status) }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 列表视图 -->
      <el-table
        v-else
        :data="seats"
        style="width: 100%"
      >
        <el-table-column prop="seatNumber" label="座位号" width="120" />
        <el-table-column prop="area" label="区域" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="primary"
              size="small"
              @click="handleReserve(row)"
            >
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 我的预约 -->
    <el-card class="reservations-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>
      <el-table :data="myReservations" style="width: 100%">
        <el-table-column prop="seatNumber" label="座位号" width="120" />
        <el-table-column prop="reserveDate" label="预约日期" width="180" />
        <el-table-column prop="timeSlot" label="时间段" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
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

    <!-- 预约对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="预约座位"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="座位号">
          <span>{{ selectedSeat?.seatNumber }}</span>
        </el-form-item>
        <el-form-item label="日期">
          <span>{{ formatDate(selectedDate) }}</span>
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="请选择时间段">
            <el-option label="上午 (8:00-12:00)" value="morning" />
            <el-option label="下午 (13:00-17:00)" value="afternoon" />
            <el-option label="晚上 (18:00-22:00)" value="evening" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确认预约
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSeats, getSeatStats, reserveSeat, cancelReservation, getMyReservations, getSeatAreas } from '@/api/seat'

// 日期选择
const selectedDate = ref(new Date())
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 统计数据
const stats = ref({
  totalSeats: 0,
  reservedSeats: 0,
  availableSeats: 0
})

// 座位列表
const seats = ref([])
const viewMode = ref('grid')
const selectedArea = ref('')
const areas = ref([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 我的预约
const myReservations = ref([])

// 预约表单
const dialogVisible = ref(false)
const selectedSeat = ref(null)
const formRef = ref(null)
const form = reactive({
  timeSlot: ''
})

const rules = {
  timeSlot: [
    { required: true, message: '请选择时间段', trigger: 'change' }
  ]
}

// 获取座位列表
const fetchSeats = async () => {
  try {
    const res = await getSeats({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      date: formatDate(selectedDate.value),
      area: selectedArea.value
    })
    seats.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取座位列表失败:', error)
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getSeatStats({
      date: formatDate(selectedDate.value)
    })
    stats.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取我的预约
const fetchMyReservations = async () => {
  try {
    const res = await getMyReservations()
    myReservations.value = res.data
  } catch (error) {
    console.error('获取预约记录失败:', error)
  }
}

// 获取区域列表
const fetchAreas = async () => {
  try {
    const res = await getSeatAreas()
    areas.value = res.data
  } catch (error) {
    console.error('获取区域列表失败:', error)
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 0:
      return 'success'
    case 1:
      return 'warning'
    case 2:
      return 'primary'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '可用'
    case 1:
      return '已预约'
    case 2:
      return '使用中'
    default:
      return '未知'
  }
}

// 格式化日期
const formatDate = (date) => {
  return date.toISOString().split('T')[0]
}

// 日期变更
const handleDateChange = () => {
  fetchSeats()
  fetchStats()
}

// 区域变更
const handleAreaChange = () => {
  currentPage.value = 1
  fetchSeats()
}

// 分页大小变更
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchSeats()
}

// 页码变更
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchSeats()
}

// 点击座位
const handleSeatClick = (seat) => {
  if (seat.status === 0) {
    selectedSeat.value = seat
    dialogVisible.value = true
  }
}

// 预约座位
const handleReserve = (seat) => {
  selectedSeat.value = seat
  dialogVisible.value = true
}

// 提交预约
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    await reserveSeat({
      seatId: selectedSeat.value.id,
      date: formatDate(selectedDate.value),
      timeSlot: form.timeSlot
    })
    ElMessage.success('预约成功')
    dialogVisible.value = false
    fetchSeats()
    fetchStats()
    fetchMyReservations()
  } catch (error) {
    console.error('预约失败:', error)
  }
}

// 取消预约
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消该预约吗？',
      '取消确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cancelReservation(row.id)
    ElMessage.success('取消成功')
    fetchSeats()
    fetchStats()
    fetchMyReservations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
    }
  }
}

onMounted(() => {
  fetchSeats()
  fetchStats()
  fetchMyReservations()
  fetchAreas()
})
</script>

<style scoped>
.seats-container {
  padding: 20px;
}

.date-card,
.seats-card,
.reservations-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    color: var(--text-color);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.seats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.seat-card {
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  &.is-reserved {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  .seat-info {
    text-align: center;
    
    .seat-number {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 10px;
    }
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 