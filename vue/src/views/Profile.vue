<template>
  <div class="profile">
    <h2>个人中心</h2>

    <div class="profile-content">
      <div class="user-info">
        <h3>个人信息</h3>
        <div class="info-item">
          <label>用户名</label>
          <span>{{ user.username }}</span>
        </div>
        <div class="info-item">
          <label>学号</label>
          <span>{{ user.studentId }}</span>
        </div>
        <div class="info-item">
          <label>邮箱</label>
          <span>{{ user.email }}</span>
        </div>
      </div>

      <div class="borrowed-books">
        <h3>借阅记录</h3>
        <div v-if="borrowedBooks.length === 0" class="empty-state">
          暂无借阅记录
        </div>
        <div v-else class="book-list">
          <div v-for="book in borrowedBooks" :key="book.id" class="book-item">
            <div class="book-info">
              <h4>{{ book.title }}</h4>
              <p>借阅时间: {{ formatDate(book.borrowDate) }}</p>
              <p>应还时间: {{ formatDate(book.dueDate) }}</p>
            </div>
            <button @click="handleReturn(book)">归还</button>
          </div>
        </div>
      </div>

      <div class="seat-reservations">
        <h3>座位预约</h3>
        <div v-if="reservations.length === 0" class="empty-state">
          暂无预约记录
        </div>
        <div v-else class="reservation-list">
          <div v-for="reservation in reservations" :key="reservation.id" class="reservation-item">
            <div class="reservation-info">
              <h4>座位 {{ reservation.seatNumber }}</h4>
              <p>预约时间: {{ formatDate(reservation.reservationDate) }}</p>
              <p>状态: {{ getReservationStatus(reservation.status) }}</p>
            </div>
            <button 
              v-if="reservation.status === 'ACTIVE'"
              @click="handleCancelReservation(reservation)"
            >
              取消预约
            </button>
          </div>
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
import { books, seats } from '../utils/api'

const user = ref({})
const borrowedBooks = ref([])
const reservations = ref([])
const loading = ref(false)
const error = ref('')

const loadUserData = async () => {
  try {
    loading.value = true
    error.value = ''
    // TODO: Implement user data loading
    // const response = await user.getProfile()
    // user.value = response.data
    // borrowedBooks.value = response.data.borrowedBooks
    // reservations.value = response.data.reservations
  } catch (err) {
    error.value = '加载用户数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const getReservationStatus = (status) => {
  const statusMap = {
    'ACTIVE': '进行中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const handleReturn = async (book) => {
  try {
    await books.return(book.id)
    await loadUserData()
  } catch (err) {
    error.value = '归还失败'
    console.error(err)
  }
}

const handleCancelReservation = async (reservation) => {
  try {
    await seats.cancel(reservation.id)
    await loadUserData()
  } catch (err) {
    error.value = '取消预约失败'
    console.error(err)
  }
}

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
.profile {
  padding: 2rem;
}

h2 {
  margin-bottom: 2rem;
  color: #2c3e50;
}

.profile-content {
  display: grid;
  gap: 2rem;
}

.user-info,
.borrowed-books,
.seat-reservations {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h3 {
  margin: 0 0 1.5rem;
  color: #2c3e50;
}

.info-item {
  margin-bottom: 1rem;
}

.info-item label {
  display: block;
  color: #666;
  margin-bottom: 0.5rem;
}

.info-item span {
  color: #2c3e50;
  font-weight: 500;
}

.empty-state {
  text-align: center;
  color: #666;
  padding: 2rem;
}

.book-list,
.reservation-list {
  display: grid;
  gap: 1rem;
}

.book-item,
.reservation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.book-info h4,
.reservation-info h4 {
  margin: 0 0 0.5rem;
  color: #2c3e50;
}

.book-info p,
.reservation-info p {
  margin: 0.25rem 0;
  color: #666;
}

button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #2c3e50;
  color: white;
  cursor: pointer;
}

button:hover {
  background-color: #34495e;
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