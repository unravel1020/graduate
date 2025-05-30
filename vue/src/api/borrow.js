import request from '@/utils/request'

// 获取借阅记录
export function getBorrowRecords(params) {
  return request({
    url: '/api/books/borrow-records',
    method: 'get',
    params
  })
}

// 借阅图书
export function borrowBook(bookId) {
  return request({
    url: `/api/books/${bookId}/borrow`,
    method: 'post'
  })
}

// 归还图书
export function returnBook(bookId) {
  return request({
    url: `/api/books/${bookId}/return`,
    method: 'post'
  })
} 