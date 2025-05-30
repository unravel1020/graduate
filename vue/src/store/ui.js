import { defineStore } from 'pinia'

export const useUIStore = defineStore('ui', {
  state: () => ({
    sidebarOpen: true,
    modalOpen: false,
    currentModal: null,
    modalData: null,
    toast: {
      show: false,
      message: '',
      type: 'info',
      duration: 3000
    },
    breadcrumbs: []
  }),

  actions: {
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen
    },

    openModal(modalName, data = null) {
      this.modalOpen = true
      this.currentModal = modalName
      this.modalData = data
    },

    closeModal() {
      this.modalOpen = false
      this.currentModal = null
      this.modalData = null
    },

    showToast(message, type = 'info', duration = 3000) {
      this.toast = {
        show: true,
        message,
        type,
        duration
      }
    },

    hideToast() {
      this.toast.show = false
    },

    setBreadcrumbs(breadcrumbs) {
      this.breadcrumbs = breadcrumbs
    },

    clearBreadcrumbs() {
      this.breadcrumbs = []
    }
  }
}) 