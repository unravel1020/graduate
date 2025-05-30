import { defineStore } from 'pinia'
import { auth } from '../utils/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    profile: null,
    borrowedBooks: [],
    reservations: [],
    loading: false,
    error: null
  }),

  getters: {
    hasActiveReservations: (state) => state.reservations.some(reservation => reservation.status === 'ACTIVE'),
    hasBorrowedBooks: (state) => state.borrowedBooks.length > 0
  },

  actions: {
    async fetchProfile() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getProfile()
        this.profile = response.data
      } catch (err) {
        this.error = '获取用户信息失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchBorrowedBooks() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getBorrowedBooks()
        this.borrowedBooks = response.data
      } catch (err) {
        this.error = '获取借阅记录失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchReservations() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getReservations()
        this.reservations = response.data
      } catch (err) {
        this.error = '获取预约记录失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async updateProfile(profileData) {
      try {
        this.loading = true
        this.error = null
        const response = await auth.updateProfile(profileData)
        this.profile = response.data
      } catch (err) {
        this.error = '更新用户信息失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async changePassword(passwordData) {
      try {
        this.loading = true
        this.error = null
        await auth.changePassword(passwordData)
      } catch (err) {
        this.error = '修改密码失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    }
  }
}) 