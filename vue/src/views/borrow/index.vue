<template>
  <div class="borrow-container">
    <!-- 借阅统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>当前借阅</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.currentBorrows }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>历史借阅</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalBorrows }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>逾期未还</span>
            </div>
          </template>
          <div class="stat-value">{{ stats.overdueBorrows }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 借阅记录 -->
    <el-card class="borrow-card">
      <template #header>
        <div class="card-header">
          <span>借阅记录</span>
          <el-radio-group v-model="activeTab" size="small">
            <el-radio-button label="current">当前借阅</el-radio-button>
            <el-radio-button label="history">历史记录</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="borrowRecords" style="width: 100%">
        <el-table-column prop="bookName" label="图书名称" />
        <el-table-column prop="borrowDate" label="借阅日期" width="180" />
        <el-table-column prop="dueDate" label="应还日期" width="180" />
        <el-table-column prop="returnDate" label="归还日期" width="180" />
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
              v-if="row.status === 'BORROWED'"
              type="primary"
              size="small"
              @click="handleReturn(row)"
            >
              归还
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBorrowRecords, returnBook } from '@/api/borrow'

// 统计数据
const stats = ref({
  currentBorrows: 0,
  totalBorrows: 0,
  overdueBorrows: 0
})

// 借阅记录
const borrowRecords = ref([])
const activeTab = ref('current')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取借阅记录
const fetchBorrowRecords = async () => {
  try {
    const res = await getBorrowRecords({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: activeTab.value === 'current' ? 'BORROWED' : undefined
    })
    borrowRecords.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取借阅记录失败:', error)
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'BORROWED':
      return 'warning'
    case 'RETURNED':
      return 'success'
    case 'OVERDUE':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'BORROWED':
      return '借阅中'
    case 'RETURNED':
      return '已归还'
    case 'OVERDUE':
      return '已逾期'
    default:
      return '未知'
  }
}

// 分页大小变更
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBorrowRecords()
}

// 页码变更
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBorrowRecords()
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要归还该图书吗？',
      '归还确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await returnBook(row.id)
    ElMessage.success('归还成功')
    fetchBorrowRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还失败:', error)
    }
  }
}

// 监听标签页切换
watch(activeTab, () => {
  currentPage.value = 1
  fetchBorrowRecords()
})

onMounted(() => {
  fetchBorrowRecords()
})
</script>

<style scoped>
.borrow-container {
  padding: 20px;
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

.borrow-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 