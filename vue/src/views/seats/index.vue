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
          <el-select v-model="areaFilter" placeholder="选择区域" @change="handleAreaChange">
            <el-option label="全部区域" value="" />
            <el-option label="A区" value="A" />
            <el-option label="B区" value="B" />
            <el-option label="C区" value="C" />
          </el-select>
        </div>
      </template>

      <el-table :data="seats" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="number" label="座位号" />
        <el-table-column prop="area" label="区域" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'available' ? 'success' : 'danger'">
              {{ row.status === 'available' ? '可预约' : '已预约' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              type="primary"
              :disabled="row.status !== 'available'"
              @click="handleReserve(row)"
            >
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
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
      width="400px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="座位号">
          <span>{{ form.seatNumber }}</span>
        </el-form-item>
        <el-form-item label="预约时间" prop="time">
          <el-date-picker
            v-model="form.time"
            type="datetime"
            placeholder="选择预约时间"
            :disabled-date="disabledDate"
            :disabled-hours="disabledHours"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
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
const loading = ref(false)
const areaFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 我的预约
const myReservations = ref([])

// 预约表单
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  seatNumber: '',
  time: null
})

const rules = {
  time: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

// 获取座位列表
const fetchSeats = async () => {
  try {
    loading.value = true
    const res = await getSeats({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      date: formatDate(selectedDate.value),
      area: areaFilter.value
    })
    seats.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取座位列表失败')
  } finally {
    loading.value = false
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
    // Assuming the response is an array of areas
    // You might want to adjust this based on your actual API response
    // For example, you can map the response to an array of options
    // const areas = res.data.map(area => ({ label: area, value: area }))
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

// 区域筛选
const handleAreaChange = () => {
  currentPage.value = 1
  fetchSeats()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchSeats()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchSeats()
}

// 预约座位
const handleReserve = (row) => {
  form.seatNumber = row.number
  form.time = null
  dialogVisible.value = true
}

// 提交预约
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    await reserveSeat({
      seatId: row.id,
      date: formatDate(selectedDate.value),
      time: form.time
    })
    ElMessage.success('预约成功')
    dialogVisible.value = false
    fetchSeats()
    fetchStats()
    fetchMyReservations()
  } catch (error) {
    ElMessage.error('预约失败')
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

// 禁用非工作时间
const disabledHours = () => {
  const hours = []
  for (let i = 0; i < 24; i++) {
    if (i < 8 || i > 22) {
      hours.push(i)
    }
  }
  return hours
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 