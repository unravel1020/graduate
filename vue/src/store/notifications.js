import { defineStore } from 'pinia'

export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    notifications: [],
    unreadCount: 0
  }),

  getters: {
    hasUnread: (state) => state.unreadCount > 0
  },

  actions: {
    addNotification(notification) {
      this.notifications.unshift({
        id: Date.now(),
        timestamp: new Date(),
        read: false,
        ...notification
      })
      this.unreadCount++
    },

    markAsRead(id) {
      const notification = this.notifications.find(n => n.id === id)
      if (notification && !notification.read) {
        notification.read = true
        this.unreadCount--
      }
    },

    markAllAsRead() {
      this.notifications.forEach(notification => {
        if (!notification.read) {
          notification.read = true
        }
      })
      this.unreadCount = 0
    },

    removeNotification(id) {
      const index = this.notifications.findIndex(n => n.id === id)
      if (index !== -1) {
        if (!this.notifications[index].read) {
          this.unreadCount--
        }
        this.notifications.splice(index, 1)
      }
    },

    clearAll() {
      this.notifications = []
      this.unreadCount = 0
    }
  }
}) 