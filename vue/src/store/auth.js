import { defineStore } from 'pinia'
import { auth } from '../utils/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: null,
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user
  },

  actions: {
    async login(username, password) {
      try {
        this.loading = true
        this.error = null
        const response = await auth.login(username, password)
        this.token = response.data
        localStorage.setItem('token', response.data)
        await this.fetchUserProfile()
      } catch (err) {
        this.error = err.response?.data?.message || '登录失败'
        throw err
      } finally {
        this.loading = false
      }
    },

    async fetchUserProfile() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getProfile()
        this.user = response.data
      } catch (err) {
        this.error = '获取用户信息失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async logout() {
      try {
        this.loading = true
        this.error = null
        await auth.logout()
      } catch (err) {
        console.error(err)
      } finally {
        this.token = null
        this.user = null
        localStorage.removeItem('token')
        this.loading = false
      }
    }
  }
}) 