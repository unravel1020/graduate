import { defineStore } from 'pinia'

export const useFilterStore = defineStore('filters', {
  state: () => ({
    bookFilters: {
      status: 'all',
      category: 'all',
      searchQuery: '',
      sortBy: 'title',
      sortOrder: 'asc'
    },
    seatFilters: {
      status: 'all',
      floor: 'all',
      searchQuery: '',
      sortBy: 'number',
      sortOrder: 'asc'
    },
    userFilters: {
      role: 'all',
      status: 'all',
      searchQuery: '',
      sortBy: 'username',
      sortOrder: 'asc'
    }
  }),

  actions: {
    updateBookFilters(filters) {
      this.bookFilters = {
        ...this.bookFilters,
        ...filters
      }
    },

    updateSeatFilters(filters) {
      this.seatFilters = {
        ...this.seatFilters,
        ...filters
      }
    },

    updateUserFilters(filters) {
      this.userFilters = {
        ...this.userFilters,
        ...filters
      }
    },

    resetBookFilters() {
      this.bookFilters = {
        status: 'all',
        category: 'all',
        searchQuery: '',
        sortBy: 'title',
        sortOrder: 'asc'
      }
    },

    resetSeatFilters() {
      this.seatFilters = {
        status: 'all',
        floor: 'all',
        searchQuery: '',
        sortBy: 'number',
        sortOrder: 'asc'
      }
    },

    resetUserFilters() {
      this.userFilters = {
        role: 'all',
        status: 'all',
        searchQuery: '',
        sortBy: 'username',
        sortOrder: 'asc'
      }
    },

    resetAllFilters() {
      this.resetBookFilters()
      this.resetSeatFilters()
      this.resetUserFilters()
    }
  }
}) 