import request from '@/utils/request'

// 获取座位列表
export function getSeats(params) {
  return request({
    url: '/api/seats',
    method: 'get',
    params
  })
}

// 获取座位详情
export function getSeatById(id) {
  return request({
    url: `/api/seats/${id}`,
    method: 'get'
  })
}

// 获取座位状态统计
export function getSeatStats(params) {
  return request({
    url: '/api/seats/stats',
    method: 'get',
    params
  })
}

// 预约座位
export function reserveSeat(data) {
  return request({
    url: '/api/seats/reserve',
    method: 'post',
    data
  })
}

// 取消预约
export function cancelReservation(id) {
  return request({
    url: `/api/seats/reservations/${id}/cancel`,
    method: 'post'
  })
}

// 获取我的预约记录
export function getMyReservations() {
  return request({
    url: '/api/seats/reservations',
    method: 'get'
  })
}

// 获取座位区域列表
export function getSeatAreas() {
  return request({
    url: '/api/seats/areas',
    method: 'get'
  })
}

// 获取座位状态
export function getSeatStatus(date) {
  return request({
    url: '/seats/status',
    method: 'get',
    params: { date }
  })
} 