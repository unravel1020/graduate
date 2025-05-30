import { defineStore } from 'pinia'

export const usePaginationStore = defineStore('pagination', {
  state: () => ({
    bookPagination: {
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      totalPages: 0
    },
    seatPagination: {
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      totalPages: 0
    },
    userPagination: {
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      totalPages: 0
    }
  }),

  actions: {
    updateBookPagination(pagination) {
      this.bookPagination = {
        ...this.bookPagination,
        ...pagination
      }
    },

    updateSeatPagination(pagination) {
      this.seatPagination = {
        ...this.seatPagination,
        ...pagination
      }
    },

    updateUserPagination(pagination) {
      this.userPagination = {
        ...this.userPagination,
        ...pagination
      }
    },

    setBookPage(page) {
      this.bookPagination.currentPage = page
    },

    setSeatPage(page) {
      this.seatPagination.currentPage = page
    },

    setUserPage(page) {
      this.userPagination.currentPage = page
    },

    setBookPageSize(size) {
      this.bookPagination.pageSize = size
      this.bookPagination.currentPage = 1
    },

    setSeatPageSize(size) {
      this.seatPagination.pageSize = size
      this.seatPagination.currentPage = 1
    },

    setUserPageSize(size) {
      this.userPagination.pageSize = size
      this.userPagination.currentPage = 1
    },

    resetBookPagination() {
      this.bookPagination = {
        currentPage: 1,
        pageSize: 10,
        totalItems: 0,
        totalPages: 0
      }
    },

    resetSeatPagination() {
      this.seatPagination = {
        currentPage: 1,
        pageSize: 10,
        totalItems: 0,
        totalPages: 0
      }
    },

    resetUserPagination() {
      this.userPagination = {
        currentPage: 1,
        pageSize: 10,
        totalItems: 0,
        totalPages: 0
      }
    },

    resetAllPagination() {
      this.resetBookPagination()
      this.resetSeatPagination()
      this.resetUserPagination()
    }
  }
}) 