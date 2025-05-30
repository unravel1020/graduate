<template>
  <div class="books-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索图书..."
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 图书列表 -->
    <el-table
      v-loading="loading"
      :data="books"
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="200" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="publisher" label="出版社" width="150" />
      <el-table-column prop="isbn" label="ISBN" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '可借阅' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            :disabled="row.status !== '可借阅'"
            @click="handleBorrow(row)"
          >
            借阅
          </el-button>
          <el-button
            type="info"
            size="small"
            @click="showBookDetail(row)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="图书详情"
      width="500px"
    >
      <div v-if="currentBook" class="book-detail">
        <div class="detail-item">
          <span class="label">书名：</span>
          <span>{{ currentBook.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">作者：</span>
          <span>{{ currentBook.author }}</span>
        </div>
        <div class="detail-item">
          <span class="label">出版社：</span>
          <span>{{ currentBook.publisher }}</span>
        </div>
        <div class="detail-item">
          <span class="label">ISBN：</span>
          <span>{{ currentBook.isbn }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="currentBook.status === '可借阅' ? 'success' : 'danger'">
            {{ currentBook.status }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">简介：</span>
          <p>{{ currentBook.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getBooks, getBook, borrowBook } from '@/api/book'

// 搜索关键词
const searchKeyword = ref('')

// 表格数据
const loading = ref(false)
const books = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 图书详情
const dialogVisible = ref(false)
const currentBook = ref(null)

// 获取图书列表
const fetchBooks = async () => {
  try {
    loading.value = true
    const res = await getBooks({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    books.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchBooks()
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

// 借阅图书
const handleBorrow = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要借阅《${row.title}》吗？`,
      '借阅确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await borrowBook(row.id)
    ElMessage.success('借阅成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('借阅失败:', error)
    }
  }
}

// 显示图书详情
const showBookDetail = async (row) => {
  try {
    const res = await getBook(row.id)
    currentBook.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error('获取图书详情失败:', error)
  }
}

onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.books-container {
  padding: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.book-detail {
  .detail-item {
    margin-bottom: 15px;
    
    .label {
      font-weight: bold;
      margin-right: 10px;
      color: var(--text-color);
    }
    
    p {
      margin: 10px 0 0;
      color: var(--text-color-secondary);
      line-height: 1.5;
    }
  }
}
</style> 