import { defineStore } from 'pinia'
import { books } from '../utils/api'

export const useBookStore = defineStore('books', {
  state: () => ({
    books: [],
    currentBook: null,
    loading: false,
    error: null
  }),

  getters: {
    availableBooks: (state) => state.books.filter(book => book.status === 'AVAILABLE'),
    borrowedBooks: (state) => state.books.filter(book => book.status === 'BORROWED')
  },

  actions: {
    async fetchBooks(query = '') {
      try {
        this.loading = true
        this.error = null
        const response = await books.search(query)
        this.books = response.data
      } catch (err) {
        this.error = '加载图书失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchBookById(id) {
      try {
        this.loading = true
        this.error = null
        const response = await books.getById(id)
        this.currentBook = response.data
      } catch (err) {
        this.error = '加载图书详情失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async borrowBook(id) {
      try {
        this.loading = true
        this.error = null
        await books.borrow(id)
        await this.fetchBooks()
      } catch (err) {
        this.error = '借阅失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async returnBook(id) {
      try {
        this.loading = true
        this.error = null
        await books.return(id)
        await this.fetchBooks()
      } catch (err) {
        this.error = '归还失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async addBook(bookData) {
      try {
        this.loading = true
        this.error = null
        await books.add(bookData)
        await this.fetchBooks()
      } catch (err) {
        this.error = '添加图书失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async updateBook(id, bookData) {
      try {
        this.loading = true
        this.error = null
        await books.update(id, bookData)
        await this.fetchBooks()
      } catch (err) {
        this.error = '更新图书失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async deleteBook(id) {
      try {
        this.loading = true
        this.error = null
        await books.delete(id)
        await this.fetchBooks()
      } catch (err) {
        this.error = '删除图书失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    }
  }
}) 