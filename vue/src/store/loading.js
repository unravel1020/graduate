import { defineStore } from 'pinia'

export const useLoadingStore = defineStore('loading', {
  state: () => ({
    loadingStates: new Map(),
    globalLoading: false
  }),

  getters: {
    isLoading: (state) => (key) => state.loadingStates.get(key) || false,
    isAnyLoading: (state) => state.globalLoading || Array.from(state.loadingStates.values()).some(Boolean)
  },

  actions: {
    startLoading(key) {
      this.loadingStates.set(key, true)
      this.updateGlobalLoading()
    },

    stopLoading(key) {
      this.loadingStates.set(key, false)
      this.updateGlobalLoading()
    },

    setGlobalLoading(loading) {
      this.globalLoading = loading
    },

    updateGlobalLoading() {
      this.globalLoading = Array.from(this.loadingStates.values()).some(Boolean)
    },

    clearAll() {
      this.loadingStates.clear()
      this.globalLoading = false
    }
  }
}) 