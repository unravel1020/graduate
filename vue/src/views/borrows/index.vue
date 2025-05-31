<template>
  <div class="borrows-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="图书">
          <el-input v-model="searchForm.bookTitle" placeholder="请输入图书名称" clearable />
        </el-form-item>
        <el-form-item label="借阅人">
          <el-input v-model="searchForm.borrowerName" placeholder="请输入借阅人姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" value="borrowed" />
            <el-option label="已归还" value="returned" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleBorrow">借阅图书</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 借阅列表 -->
    <el-card class="table-card">
      <el-table :data="borrowList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookTitle" label="图书名称" />
        <el-table-column prop="borrowerName" label="借阅人" />
        <el-table-column prop="borrowDate" label="借阅日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.borrowDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应还日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.returnDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'borrowed'"
              type="success"
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 借阅图书对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="借阅图书"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="图书" prop="bookId">
          <el-select
            v-model="form.bookId"
            filterable
            remote
            :remote-method="searchBooks"
            :loading="searchLoading"
            placeholder="请输入图书名称搜索"
          >
            <el-option
              v-for="item in bookOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number
            v-model="form.days"
            :min="1"
            :max="30"
            :step="1"
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { borrows } from '@/utils/api'
import { books } from '@/utils/api'

// 搜索表单
const searchForm = reactive({
  bookTitle: '',
  borrowerName: '',
  status: ''
})

// 表格数据
const borrowList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  bookId: '',
  days: 7
})

// 图书搜索
const searchLoading = ref(false)
const bookOptions = ref([])

// 表单验证规则
const rules = {
  bookId: [
    { required: true, message: '请选择图书', trigger: 'change' }
  ],
  days: [
    { required: true, message: '请输入借阅天数', trigger: 'blur' }
  ]
}

// 获取借阅列表
const fetchBorrows = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const res = await borrows.getList(params)
    if (res.code === 200) {
      borrowList.value = res.data.list
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取借阅列表失败')
    }
  } catch (error) {
    console.error('获取借阅列表失败:', error)
    ElMessage.error('获取借阅列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索图书
const searchBooks = async (query) => {
  if (query) {
    searchLoading.value = true
    try {
      const res = await books.search({ title: query })
      if (res.code === 200) {
        bookOptions.value = res.data
      } else {
        ElMessage.error(res.message || '搜索图书失败')
      }
    } catch (error) {
      console.error('搜索图书失败:', error)
      ElMessage.error('搜索图书失败')
    } finally {
      searchLoading.value = false
    }
  } else {
    bookOptions.value = []
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchBorrows()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 借阅图书
const handleBorrow = () => {
  form.bookId = ''
  form.days = 7
  dialogVisible.value = true
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确定要归还该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await borrows.return(row.id)
    if (res.code === 200) {
      ElMessage.success('归还成功')
      fetchBorrows()
    } else {
      ElMessage.error(res.message || '归还失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还失败:', error)
      ElMessage.error('归还失败')
    }
  }
}

// 提交借阅
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const res = await borrows.create(form)
    if (res.code === 200) {
      ElMessage.success('借阅成功')
      dialogVisible.value = false
      fetchBorrows()
    } else {
      ElMessage.error(res.message || '借阅失败')
    }
  } catch (error) {
    console.error('借阅失败:', error)
    ElMessage.error('借阅失败')
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBorrows()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBorrows()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'borrowed':
      return 'warning'
    case 'returned':
      return 'success'
    case 'overdue':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'borrowed':
      return '借阅中'
    case 'returned':
      return '已归还'
    case 'overdue':
      return '已逾期'
    default:
      return '未知'
  }
}

// 初始化
onMounted(() => {
  fetchBorrows()
})
</script>

<style scoped>
.borrows-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 