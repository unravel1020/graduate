<template>
  <div class="book-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <h2>{{ book.title }}</h2>
          <el-button
            v-if="book.availableCopies > 0"
            type="primary"
            @click="handleBorrow"
            :loading="borrowing"
          >
            借阅
          </el-button>
          <el-button
            v-else
            type="info"
            disabled
          >
            暂无可借
          </el-button>
        </div>
      </template>

      <div class="book-info">
        <div class="info-item">
          <span class="label">作者：</span>
          <span>{{ book.author }}</span>
        </div>
        <div class="info-item">
          <span class="label">ISBN：</span>
          <span>{{ book.isbn }}</span>
        </div>
        <div class="info-item">
          <span class="label">出版社：</span>
          <span>{{ book.publisher }}</span>
        </div>
        <div class="info-item">
          <span class="label">出版日期：</span>
          <span>{{ book.publishDate }}</span>
        </div>
        <div class="info-item">
          <span class="label">分类：</span>
          <span>{{ book.category }}</span>
        </div>
        <div class="info-item">
          <span class="label">可借数量：</span>
          <span>{{ book.availableCopies }}</span>
        </div>
        <div class="info-item">
          <span class="label">总数量：</span>
          <span>{{ book.totalCopies }}</span>
        </div>
      </div>

      <div class="book-description">
        <h3>图书简介</h3>
        <p>{{ book.description }}</p>
      </div>
    </el-card>

    <el-dialog
      v-model="borrowDialogVisible"
      title="确认借阅"
      width="30%"
    >
      <span>确认借阅《{{ book.title }}》？</span>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useBorrowRecordStore } from '../store/borrowRecords'
import api from '../utils/api'

const route = useRoute()
const router = useRouter()
const borrowRecordStore = useBorrowRecordStore()

const book = ref({})
const loading = ref(false)
const borrowing = ref(false)
const borrowDialogVisible = ref(false)

const fetchBookDetail = async () => {
  try {
    loading.value = true
    const response = await api.get(`/books/${route.params.id}`)
    book.value = response.data
  } catch (error) {
    ElMessage.error('获取图书详情失败')
    router.push('/books')
  } finally {
    loading.value = false
  }
}

const handleBorrow = () => {
  borrowDialogVisible.value = true
}

const confirmBorrow = async () => {
  try {
    borrowing.value = true
    await borrowRecordStore.borrowBook(book.value.id)
    ElMessage.success('借阅成功')
    borrowDialogVisible.value = false
    await fetchBookDetail()
  } catch (error) {
    ElMessage.error(error.message || '借阅失败')
  } finally {
    borrowing.value = false
  }
}

onMounted(fetchBookDetail)
</script>

<style scoped>
.book-detail {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-info {
  margin: 20px 0;
}

.info-item {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.label {
  font-weight: bold;
  width: 100px;
  color: #606266;
}

.book-description {
  margin-top: 20px;
}

.book-description h3 {
  margin-bottom: 10px;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 