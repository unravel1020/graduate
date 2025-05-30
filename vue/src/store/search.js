import { defineStore } from 'pinia'

export const useSearchStore = defineStore('search', {
  state: () => ({
    bookSearch: {
      query: '',
      results: [],
      loading: false,
      error: null
    },
    seatSearch: {
      query: '',
      results: [],
      loading: false,
      error: null
    },
    userSearch: {
      query: '',
      results: [],
      loading: false,
      error: null
    }
  }),

  actions: {
    updateBookSearch(query) {
      this.bookSearch.query = query
    },

    updateSeatSearch(query) {
      this.seatSearch.query = query
    },

    updateUserSearch(query) {
      this.userSearch.query = query
    },

    setBookResults(results) {
      this.bookSearch.results = results
    },

    setSeatResults(results) {
      this.seatSearch.results = results
    },

    setUserResults(results) {
      this.userSearch.results = results
    },

    setBookLoading(loading) {
      this.bookSearch.loading = loading
    },

    setSeatLoading(loading) {
      this.seatSearch.loading = loading
    },

    setUserLoading(loading) {
      this.userSearch.loading = loading
    },

    setBookError(error) {
      this.bookSearch.error = error
    },

    setSeatError(error) {
      this.seatSearch.error = error
    },

    setUserError(error) {
      this.userSearch.error = error
    },

    resetBookSearch() {
      this.bookSearch = {
        query: '',
        results: [],
        loading: false,
        error: null
      }
    },

    resetSeatSearch() {
      this.seatSearch = {
        query: '',
        results: [],
        loading: false,
        error: null
      }
    },

    resetUserSearch() {
      this.userSearch = {
        query: '',
        results: [],
        loading: false,
        error: null
      }
    },

    resetAllSearch() {
      this.resetBookSearch()
      this.resetSeatSearch()
      this.resetUserSearch()
    }
  }
}) 