<template>
  <div class="books-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="searchForm.isbn" placeholder="请输入ISBN" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="handleAdd">添加图书</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图书列表 -->
    <el-card class="table-card">
      <el-table :data="bookList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="publisher" label="出版社" />
        <el-table-column prop="publishDate" label="出版日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.publishDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'available' ? 'success' : 'danger'">
              {{ row.status === 'available' ? '可借阅' : '已借出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
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

    <!-- 添加/编辑图书对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加图书' : '编辑图书'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="form.isbn" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="form.publishDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="可借阅" value="available" />
            <el-option label="已借出" value="borrowed" />
          </el-select>
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
import { books } from '@/utils/api'

// 搜索表单
const searchForm = reactive({
  title: '',
  author: '',
  isbn: ''
})

// 表格数据
const bookList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = reactive({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  publishDate: '',
  status: 'available'
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  isbn: [
    { required: true, message: '请输入ISBN', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入出版社', trigger: 'blur' }
  ],
  publishDate: [
    { required: true, message: '请选择出版日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取图书列表
const fetchBooks = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const res = await books.getList(params)
    bookList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 添加图书
const handleAdd = () => {
  dialogType.value = 'add'
  Object.keys(form).forEach(key => {
    form[key] = key === 'status' ? 'available' : ''
  })
  dialogVisible.value = true
}

// 编辑图书
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  dialogVisible.value = true
}

// 删除图书
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该图书吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await books.delete(row.id)
      ElMessage.success('删除成功')
      fetchBooks()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    if (dialogType.value === 'add') {
      await books.create(form)
      ElMessage.success('添加成功')
    } else {
      await books.update(form.id, form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchBooks()
  } catch (error) {
    ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchBooks()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchBooks()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString()
}

// 初始化
onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.books-container {
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