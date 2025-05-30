import { defineStore } from 'pinia'
import api from '../utils/api'

export const useBorrowRecordStore = defineStore('borrowRecords', {
  state: () => ({
    records: [],
    loading: false,
    error: null
  }),

  getters: {
    activeRecords: (state) => state.records.filter(record => record.status === 'BORROWED'),
    overdueRecords: (state) => state.records.filter(record => record.status === 'OVERDUE')
  },

  actions: {
    async fetchRecords() {
      try {
        this.loading = true
        this.error = null
        const response = await api.get('/borrow-records/my')
        this.records = response.data
      } catch (err) {
        this.error = '获取借阅记录失败'
        throw err
      } finally {
        this.loading = false
      }
    },

    async borrowBook(bookId) {
      try {
        this.loading = true
        this.error = null
        const response = await api.post(`/borrow-records/${bookId}/borrow`)
        await this.fetchRecords()
        return response.data
      } catch (err) {
        this.error = '借阅失败'
        throw err
      } finally {
        this.loading = false
      }
    },

    async returnBook(recordId) {
      try {
        this.loading = true
        this.error = null
        const response = await api.post(`/borrow-records/${recordId}/return`)
        await this.fetchRecords()
        return response.data
      } catch (err) {
        this.error = '归还失败'
        throw err
      } finally {
        this.loading = false
      }
    },

    async fetchAllRecords() {
      try {
        this.loading = true
        this.error = null
        const response = await api.get('/borrow-records/admin/all')
        this.records = response.data
      } catch (err) {
        this.error = '获取所有借阅记录失败'
        throw err
      } finally {
        this.loading = false
      }
    },

    async fetchOverdueRecords() {
      try {
        this.loading = true
        this.error = null
        const response = await api.get('/borrow-records/admin/overdue')
        this.records = response.data
      } catch (err) {
        this.error = '获取逾期记录失败'
        throw err
      } finally {
        this.loading = false
      }
    }
  }
}) 