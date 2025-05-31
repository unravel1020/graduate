export default [
  {
    url: '/api/books',
    method: 'get',
    response: ({ query }) => {
      const { page = 1, size = 10, title = '', author = '', isbn = '' } = query
      const bookList = Array.from({ length: 20 }, (_, index) => ({
        id: index + 1,
        title: `图书${index + 1}`,
        author: `作者${index + 1}`,
        isbn: `9787${String(index + 1).padStart(9, '0')}`,
        publisher: `出版社${index + 1}`,
        publishDate: '2024-01-01',
        status: index % 2 === 0 ? 'available' : 'borrowed'
      }))

      let filteredList = bookList
      if (title) {
        filteredList = filteredList.filter(book => book.title.includes(title))
      }
      if (author) {
        filteredList = filteredList.filter(book => book.author.includes(author))
      }
      if (isbn) {
        filteredList = filteredList.filter(book => book.isbn.includes(isbn))
      }

      const start = (page - 1) * size
      const end = start + size
      const list = filteredList.slice(start, end)

      return {
        code: 200,
        message: 'success',
        data: {
          list,
          total: filteredList.length,
          page,
          size
        }
      }
    }
  },
  {
    url: '/api/books/search',
    method: 'get',
    response: ({ query }) => {
      const { title = '' } = query
      const bookList = Array.from({ length: 20 }, (_, index) => ({
        id: index + 1,
        title: `图书${index + 1}`,
        author: `作者${index + 1}`,
        isbn: `9787${String(index + 1).padStart(9, '0')}`,
        publisher: `出版社${index + 1}`,
        publishDate: '2024-01-01',
        status: index % 2 === 0 ? 'available' : 'borrowed'
      }))

      const filteredList = bookList.filter(book => 
        book.title.includes(title) && book.status === 'available'
      )

      return {
        code: 200,
        message: 'success',
        data: filteredList
      }
    }
  },
  {
    url: '/api/borrows',
    method: 'get',
    response: ({ query }) => {
      const { page = 1, size = 10, bookTitle = '', borrowerName = '', status = '' } = query
      const borrowList = Array.from({ length: 20 }, (_, index) => ({
        id: index + 1,
        bookId: index + 1,
        bookTitle: `图书${index + 1}`,
        borrowerId: 1,
        borrowerName: 'admin',
        borrowDate: '2024-01-01',
        dueDate: '2024-02-01',
        returnDate: index % 2 === 0 ? '2024-01-15' : null,
        status: index % 3 === 0 ? 'borrowed' : index % 3 === 1 ? 'returned' : 'overdue'
      }))

      let filteredList = borrowList
      if (bookTitle) {
        filteredList = filteredList.filter(borrow => borrow.bookTitle.includes(bookTitle))
      }
      if (borrowerName) {
        filteredList = filteredList.filter(borrow => borrow.borrowerName.includes(borrowerName))
      }
      if (status) {
        filteredList = filteredList.filter(borrow => borrow.status === status)
      }

      const start = (page - 1) * size
      const end = start + size
      const list = filteredList.slice(start, end)

      return {
        code: 200,
        message: 'success',
        data: {
          list,
          total: filteredList.length,
          page,
          size
        }
      }
    }
  },
  {
    url: '/api/borrows',
    method: 'post',
    response: ({ body }) => {
      const { bookId, days } = body
      return {
        code: 200,
        message: 'success',
        data: {
          id: Math.floor(Math.random() * 1000),
          bookId,
          bookTitle: `图书${bookId}`,
          borrowerId: 1,
          borrowerName: 'admin',
          borrowDate: new Date().toISOString().split('T')[0],
          dueDate: new Date(Date.now() + days * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
          returnDate: null,
          status: 'borrowed'
        }
      }
    }
  },
  {
    url: '/api/borrows/:id/return',
    method: 'post',
    response: ({ params }) => {
      return {
        code: 200,
        message: 'success',
        data: {
          id: parseInt(params.id),
          returnDate: new Date().toISOString().split('T')[0],
          status: 'returned'
        }
      }
    }
  },
  {
    url: '/api/borrows/overdue',
    method: 'get',
    response: () => {
      return {
        code: 200,
        message: 'success',
        data: Array.from({ length: 5 }, (_, index) => ({
          id: index + 1,
          bookId: index + 1,
          bookTitle: `图书${index + 1}`,
          borrowerId: 1,
          borrowerName: 'admin',
          borrowDate: '2024-01-01',
          dueDate: '2024-01-15',
          returnDate: null,
          status: 'overdue'
        }))
      }
    }
  },
  {
    url: '/api/auth/profile',
    method: 'get',
    response: () => {
      return {
        code: 200,
        message: 'success',
        data: {
          id: 1,
          username: 'admin',
          role: 'admin',
          avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
        }
      }
    }
  },
  {
    url: '/api/auth/login',
    method: 'post',
    response: ({ body }) => {
      const { username, password } = body
      if (username === 'admin' && password === 'admin123') {
        const userInfo = {
          id: 1,
          username: 'admin',
          role: 'admin',
          avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
        }
        return {
          code: 200,
          message: 'success',
          data: {
            token: 'mock-token',
            user: userInfo
          }
        }
      }
      return {
        code: 401,
        message: '用户名或密码错误'
      }
    }
  },
  {
    url: '/api/seats',
    method: 'get',
    response: ({ query }) => {
      const { pageNum = 1, pageSize = 10, date, area } = query
      const list = Array.from({ length: 50 }, (_, index) => ({
        id: index + 1,
        number: `A${String(index + 1).padStart(3, '0')}`,
        area: 'A',
        status: Math.random() > 0.5 ? 'available' : 'reserved'
      }))

      const filteredList = list.filter(item => {
        if (area && item.area !== area) return false
        return true
      })

      return {
        code: 200,
        data: {
          list: filteredList.slice((pageNum - 1) * pageSize, pageNum * pageSize),
          total: filteredList.length
        }
      }
    }
  },
  {
    url: '/api/seats/stats',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: {
          totalSeats: 50,
          reservedSeats: 20,
          availableSeats: 30
        }
      }
    }
  },
  {
    url: '/api/seats/areas',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: ['A', 'B', 'C']
      }
    }
  },
  {
    url: '/api/seats/reservations',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: Array.from({ length: 5 }, (_, index) => ({
          id: index + 1,
          seatNumber: `A${String(index + 1).padStart(3, '0')}`,
          reserveDate: new Date().toISOString().split('T')[0],
          timeSlot: '09:00-12:00',
          status: Math.random() > 0.5 ? 1 : 2
        }))
      }
    }
  },
  {
    url: '/api/seats/reserve',
    method: 'post',
    response: ({ body }) => {
      return {
        code: 200,
        data: {
          id: Date.now(),
          ...body,
          status: 1
        }
      }
    }
  },
  {
    url: '/api/seats/cancel/:id',
    method: 'post',
    response: () => {
      return {
        code: 200,
        data: null
      }
    }
  }
] 