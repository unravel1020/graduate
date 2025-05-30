import request from '@/utils/request'

// 获取图书列表
export function getBooks(params) {
  return request({
    url: '/books',
    method: 'get',
    params
  })
}

// 获取图书详情
export function getBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

// 借阅图书
export function borrowBook(id) {
  return request({
    url: `/books/${id}/borrow`,
    method: 'post'
  })
}

// 归还图书
export function returnBook(id) {
  return request({
    url: `/books/${id}/return`,
    method: 'post'
  })
}

// 获取借阅记录
export function getBorrowRecords(params) {
  return request({
    url: '/books/borrow-records',
    method: 'get',
    params
  })
}

// 搜索图书
export function searchBooks(keyword) {
  return request({
    url: '/books/search',
    method: 'get',
    params: { keyword }
  })
} 