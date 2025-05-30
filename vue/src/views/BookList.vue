<template>
  <div class="book-list">
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="搜索图书..."
        @input="handleSearch"
      >
    </div>

    <div class="books-grid">
      <div v-for="book in books" :key="book.id" class="book-card">
        <h3>{{ book.title }}</h3>
        <p class="author">作者: {{ book.author }}</p>
        <p class="isbn">ISBN: {{ book.isbn }}</p>
        <p class="status" :class="book.status.toLowerCase()">
          状态: {{ book.status }}
        </p>
        <div class="actions">
          <button 
            @click="handleBorrow(book)"
            :disabled="book.status !== 'AVAILABLE'"
          >
            借阅
          </button>
          <button 
            @click="handleReturn(book)"
            :disabled="book.status !== 'BORROWED'"
          >
            归还
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      加载中...
    </div>

    <div v-if="error" class="error">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { books } from '../utils/api'

const searchQuery = ref('')
const books = ref([])
const loading = ref(false)
const error = ref('')

const loadBooks = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await books.search(searchQuery.value)
    books.value = response.data
  } catch (err) {
    error.value = '加载图书失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadBooks()
}

const handleBorrow = async (book) => {
  try {
    await books.borrow(book.id)
    await loadBooks()
  } catch (err) {
    error.value = '借阅失败'
    console.error(err)
  }
}

const handleReturn = async (book) => {
  try {
    await books.return(book.id)
    await loadBooks()
  } catch (err) {
    error.value = '归还失败'
    console.error(err)
  }
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-list {
  padding: 2rem;
}

.search-bar {
  margin-bottom: 2rem;
}

.search-bar input {
  width: 100%;
  max-width: 500px;
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
}

.book-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.book-card h3 {
  margin: 0 0 1rem;
  color: #2c3e50;
}

.book-card p {
  margin: 0.5rem 0;
  color: #666;
}

.status {
  font-weight: bold;
}

.status.available {
  color: #27ae60;
}

.status.borrowed {
  color: #e74c3c;
}

.actions {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}

.actions button {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  background-color: #2c3e50;
  color: white;
  cursor: pointer;
}

.actions button:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.error {
  text-align: center;
  padding: 2rem;
  color: #e74c3c;
}
</style> 