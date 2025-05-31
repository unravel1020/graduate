import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:3000/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token')
          localStorage.removeItem('userRole')
          window.location.href = '/login'
          break
        case 403:
          window.location.href = '/403'
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// 认证相关接口
export const auth = {
  // 登录
  login: (data) => request.post('/auth/login', data),
  
  // 注册
  register: (data) => request.post('/auth/register', data),
  
  // 获取用户信息
  getProfile: () => request.get('/auth/profile')
}

// 用户相关接口
export const users = {
  // 获取用户列表
  getList: (params) => request.get('/users', { params }),
  
  // 创建用户
  create: (data) => request.post('/users', data),
  
  // 更新用户信息
  update: (id, data) => request.put(`/users/${id}`, data),
  
  // 删除用户
  delete: (id) => request.delete(`/users/${id}`)
}

// 座位预约相关接口
export const seats = {
  // 获取座位列表
  getList: (params) => request.get('/seats', { params }),
  
  // 获取座位详情
  getDetail: (id) => request.get(`/seats/${id}`),
  
  // 获取座位统计数据
  getStats: () => request.get('/seats/stats'),
  
  // 获取我的预约记录
  getMyReservations: () => request.get('/seats/my-reservations'),
  
  // 创建预约
  createReservation: (data) => request.post('/seats/reservations', data),
  
  // 取消预约
  cancelReservation: (id) => request.delete(`/seats/reservations/${id}`),
  
  // 获取预约记录
  getReservations: (params) => request.get('/seats/reservations', { params }),
  
  // 预约座位
  reserve: (id, data) => request.post(`/seats/${id}/reserve`, data),
  
  // 取消预约
  cancel: (id) => request.post(`/seats/${id}/cancel`)
}

// 图书相关接口
export const books = {
  // 获取图书列表
  getList: (params) => request.get('/books', { params }),
  
  // 创建图书
  create: (data) => request.post('/books', data),
  
  // 更新图书信息
  update: (id, data) => request.put(`/books/${id}`, data),
  
  // 删除图书
  delete: (id) => request.delete(`/books/${id}`),
  
  // 搜索图书
  search: (params) => request.get('/books/search', { params })
}

// 借阅相关接口
export const borrows = {
  // 获取借阅列表
  getList: (params) => request.get('/borrows', { params }),
  
  // 创建借阅记录
  create: (data) => request.post('/borrows', data),
  
  // 归还图书
  return: (id) => request.post(`/borrows/${id}/return`),
  
  // 获取逾期借阅记录
  getOverdue: () => request.get('/borrows/overdue')
}

export default request 