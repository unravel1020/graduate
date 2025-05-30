<template>
  <div class="borrow-records">
    <h2>我的借阅记录</h2>

    <div class="filters">
      <el-select v-model="statusFilter" placeholder="借阅状态" clearable>
        <el-option label="全部" value="" />
        <el-option label="借阅中" value="BORROWED" />
        <el-option label="已归还" value="RETURNED" />
        <el-option label="已逾期" value="OVERDUE" />
      </el-select>
    </div>

    <el-table
      v-loading="loading"
      :data="filteredRecords"
      style="width: 100%"
    >
      <el-table-column prop="book.title" label="图书名称" />
      <el-table-column prop="borrowTime" label="借阅时间" />
      <el-table-column prop="dueTime" label="应还时间" />
      <el-table-column prop="returnTime" label="归还时间" />
      <el-table-column label="状态">
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

    <el-dialog
      v-model="returnDialogVisible"
      title="确认归还"
      width="30%"
    >
      <span>确认归还《{{ selectedBook?.title }}》？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="returnDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmReturn">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useBorrowRecordStore } from '../store/borrowRecords'

const borrowRecordStore = useBorrowRecordStore()
const loading = ref(false)
const statusFilter = ref('')
const returnDialogVisible = ref(false)
const selectedBook = ref(null)

const filteredRecords = computed(() => {
  if (!statusFilter.value) {
    return borrowRecordStore.records
  }
  return borrowRecordStore.records.filter(record => record.status === statusFilter.value)
})

const getStatusType = (status) => {
  const types = {
    'BORROWED': 'primary',
    'RETURNED': 'success',
    'OVERDUE': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'BORROWED': '借阅中',
    'RETURNED': '已归还',
    'OVERDUE': '已逾期'
  }
  return texts[status] || status
}

const handleReturn = (record) => {
  selectedBook.value = record
  returnDialogVisible.value = true
}

const confirmReturn = async () => {
  try {
    await borrowRecordStore.returnBook(selectedBook.value.id)
    ElMessage.success('归还成功')
    returnDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '归还失败')
  }
}

onMounted(async () => {
  try {
    loading.value = true
    await borrowRecordStore.fetchRecords()
  } catch (error) {
    ElMessage.error('加载借阅记录失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.borrow-records {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.filters {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 