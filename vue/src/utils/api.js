import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

// Request interceptor
api.interceptors.request.use(
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

// Response interceptor
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const auth = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (data) => api.post('/auth/register', data)
}

export const books = {
  search: (params) => api.get('/books/search', { params }),
  getById: (id) => api.get(`/books/${id}`),
  add: (data) => api.post('/books', data),
  update: (id, data) => api.put(`/books/${id}`, data),
  delete: (id) => api.delete(`/books/${id}`)
}

export const seats = {
  getAvailable: () => api.get('/seats/available'),
  getById: (id) => api.get(`/seats/${id}`),
  reserve: (id, data) => api.post(`/seats/${id}/reserve`, data),
  cancelReservation: (id) => api.post(`/seats/reservations/${id}/cancel`),
  getMyReservations: () => api.get('/seats/reservations/my')
}

export default api 