import { defineStore } from 'pinia'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    theme: localStorage.getItem('theme') || 'light',
    language: localStorage.getItem('language') || 'zh-CN',
    notifications: {
      email: true,
      browser: true,
      sound: false
    },
    display: {
      showBorrowedBooks: true,
      showReservations: true,
      showNotifications: true
    }
  }),

  actions: {
    setTheme(theme) {
      this.theme = theme
      localStorage.setItem('theme', theme)
      document.documentElement.setAttribute('data-theme', theme)
    },

    setLanguage(language) {
      this.language = language
      localStorage.setItem('language', language)
    },

    updateNotificationSettings(settings) {
      this.notifications = {
        ...this.notifications,
        ...settings
      }
      localStorage.setItem('notificationSettings', JSON.stringify(this.notifications))
    },

    updateDisplaySettings(settings) {
      this.display = {
        ...this.display,
        ...settings
      }
      localStorage.setItem('displaySettings', JSON.stringify(this.display))
    },

    resetSettings() {
      this.theme = 'light'
      this.language = 'zh-CN'
      this.notifications = {
        email: true,
        browser: true,
        sound: false
      }
      this.display = {
        showBorrowedBooks: true,
        showReservations: true,
        showNotifications: true
      }
      localStorage.removeItem('theme')
      localStorage.removeItem('language')
      localStorage.removeItem('notificationSettings')
      localStorage.removeItem('displaySettings')
      document.documentElement.setAttribute('data-theme', 'light')
    }
  }
}) 