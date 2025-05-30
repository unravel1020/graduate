import { defineStore } from 'pinia'
import { auth } from '../utils/api'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    users: [],
    statistics: {
      totalBooks: 0,
      totalUsers: 0,
      totalSeats: 0,
      activeReservations: 0,
      borrowedBooks: 0
    },
    loading: false,
    error: null
  }),

  actions: {
    async fetchUsers() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getUsers()
        this.users = response.data
      } catch (err) {
        this.error = '获取用户列表失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchStatistics() {
      try {
        this.loading = true
        this.error = null
        const response = await auth.getStatistics()
        this.statistics = response.data
      } catch (err) {
        this.error = '获取统计数据失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async addUser(userData) {
      try {
        this.loading = true
        this.error = null
        await auth.addUser(userData)
        await this.fetchUsers()
      } catch (err) {
        this.error = '添加用户失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async updateUser(id, userData) {
      try {
        this.loading = true
        this.error = null
        await auth.updateUser(id, userData)
        await this.fetchUsers()
      } catch (err) {
        this.error = '更新用户失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async deleteUser(id) {
      try {
        this.loading = true
        this.error = null
        await auth.deleteUser(id)
        await this.fetchUsers()
      } catch (err) {
        this.error = '删除用户失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async resetUserPassword(id) {
      try {
        this.loading = true
        this.error = null
        await auth.resetUserPassword(id)
      } catch (err) {
        this.error = '重置密码失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    }
  }
}) 