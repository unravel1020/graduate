<template>
  <div class="books">
    <div class="header">
      <h2>图书列表</h2>
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图书..."
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="books"
      style="width: 100%"
    >
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column prop="category" label="分类" />
      <el-table-column label="可借数量">
        <template #default="{ row }">
          <el-tag :type="row.availableCopies > 0 ? 'success' : 'danger'">
            {{ row.availableCopies }}/{{ row.totalCopies }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="viewDetail(row.id)"
          >
            查看详情
          </el-button>
          <el-button
            v-if="row.availableCopies > 0"
            type="success"
            size="small"
            @click="handleBorrow(row)"
            :loading="borrowingId === row.id"
          >
            借阅
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="borrowDialogVisible"
      title="确认借阅"
      width="30%"
    >
      <span>确认借阅《{{ selectedBook?.title }}》？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="borrowDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBorrow">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useBorrowRecordStore } from '../store/borrowRecords'
import api from '../utils/api'

const router = useRouter()
const borrowRecordStore = useBorrowRecordStore()

const books = ref([])
const loading = ref(false)
const searchQuery = ref('')
const borrowDialogVisible = ref(false)
const selectedBook = ref(null)
const borrowingId = ref(null)

const fetchBooks = async () => {
  try {
    loading.value = true
    const response = await api.get('/books', {
      params: {
        query: searchQuery.value
      }
    })
    books.value = response.data
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchBooks()
}

const viewDetail = (id) => {
  router.push(`/books/${id}`)
}

const handleBorrow = (book) => {
  selectedBook.value = book
  borrowDialogVisible.value = true
}

const confirmBorrow = async () => {
  try {
    borrowingId.value = selectedBook.value.id
    await borrowRecordStore.borrowBook(selectedBook.value.id)
    ElMessage.success('借阅成功')
    borrowDialogVisible.value = false
    await fetchBooks()
  } catch (error) {
    ElMessage.error(error.message || '借阅失败')
  } finally {
    borrowingId.value = null
  }
}

onMounted(fetchBooks)
</script>

<style scoped>
.books {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  width: 300px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 