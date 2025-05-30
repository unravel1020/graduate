import { defineStore } from 'pinia'
import { seats } from '../utils/api'

export const useSeatStore = defineStore('seats', {
  state: () => ({
    seats: [],
    currentSeat: null,
    reservations: [],
    loading: false,
    error: null
  }),

  getters: {
    availableSeats: (state) => state.seats.filter(seat => seat.status === 'AVAILABLE'),
    reservedSeats: (state) => state.seats.filter(seat => seat.status === 'RESERVED'),
    occupiedSeats: (state) => state.seats.filter(seat => seat.status === 'OCCUPIED'),
    activeReservations: (state) => state.reservations.filter(reservation => reservation.status === 'ACTIVE')
  },

  actions: {
    async fetchSeats() {
      try {
        this.loading = true
        this.error = null
        const response = await seats.getAvailable()
        this.seats = response.data
      } catch (err) {
        this.error = '加载座位信息失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchSeatById(id) {
      try {
        this.loading = true
        this.error = null
        const response = await seats.getById(id)
        this.currentSeat = response.data
      } catch (err) {
        this.error = '加载座位详情失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async fetchUserReservations() {
      try {
        this.loading = true
        this.error = null
        const response = await seats.getUserReservations()
        this.reservations = response.data
      } catch (err) {
        this.error = '加载预约记录失败'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async reserveSeat(id) {
      try {
        this.loading = true
        this.error = null
        await seats.reserve(id)
        await this.fetchSeats()
        await this.fetchUserReservations()
      } catch (err) {
        this.error = '预约失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async cancelReservation(id) {
      try {
        this.loading = true
        this.error = null
        await seats.cancel(id)
        await this.fetchSeats()
        await this.fetchUserReservations()
      } catch (err) {
        this.error = '取消预约失败'
        console.error(err)
        throw err
      } finally {
        this.loading = false
      }
    }
  }
}) 