import request from '@/utils/request'

// 登录
export function login(data) {
  return Promise.resolve({
    data: {
      code: 200,
      message: '操作成功',
      data: {
        token: 'mock-token'
      }
    }
  })
}

// 注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/api/auth/current-user',
    method: 'get'
  })
} 