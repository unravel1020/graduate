package org.example.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.springboot.entity.Book;
import org.example.springboot.entity.BorrowRecord;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.BookMapper;
import org.example.springboot.mapper.BorrowRecordMapper;
import org.example.springboot.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public BookServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    public Book findById(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return book;
    }

    @Override
    public Book findByIsbn(String isbn) {
        Book book = bookMapper.findByIsbn(isbn);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return book;
    }

    @Override
    public PageInfo<Book> findAll(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Book> books = bookMapper.findAll();
        return new PageInfo<>(books);
    }

    @Override
    public PageInfo<Book> search(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Book> books = bookMapper.search(keyword);
        return new PageInfo<>(books);
    }

    @Override
    @Transactional
    public void add(Book book) {
        if (bookMapper.findByIsbn(book.getIsbn()) != null) {
            throw new BusinessException(400, "ISBN已存在");
        }
        book.setStatus(1); // 1: 可借阅
        book.setCreateTime(LocalDateTime.now());
        bookMapper.insert(book);
    }

    @Override
    @Transactional
    public void update(Book book) {
        Book existingBook = bookMapper.findById(book.getId());
        if (existingBook == null) {
            throw new BusinessException(404, "图书不存在");
        }
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.update(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        if (book.getStatus() == 2) { // 2: 已借出
            throw new BusinessException(400, "图书已借出，无法删除");
        }
        bookMapper.delete(id);
    }

    @Override
    @Transactional
    public void borrow(Long bookId, Long userId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        if (book.getStatus() != 1) {
            throw new BusinessException(400, "图书不可借阅");
        }

        // 检查用户是否有未归还的相同图书
        List<BorrowRecord> records = borrowRecordMapper.findByUserIdAndBookId(userId, bookId);
        if (!records.isEmpty() && records.stream().anyMatch(r -> r.getReturnTime() == null)) {
            throw new BusinessException(400, "您已借阅此书且未归还");
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(userId);
        record.setBorrowTime(LocalDateTime.now());
        record.setStatus(1); // 1: 借阅中
        borrowRecordMapper.insert(record);

        // 更新图书状态
        book.setStatus(2); // 2: 已借出
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.update(book);
    }

    @Override
    @Transactional
    public void returnBook(Long bookId, Long userId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }

        // 查找借阅记录
        List<BorrowRecord> records = borrowRecordMapper.findByUserIdAndBookId(userId, bookId);
        if (records.isEmpty() || records.stream().noneMatch(r -> r.getReturnTime() == null)) {
            throw new BusinessException(400, "未找到有效的借阅记录");
        }

        // 更新借阅记录
        BorrowRecord record = records.stream()
                .filter(r -> r.getReturnTime() == null)
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "未找到有效的借阅记录"));
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(2); // 2: 已归还
        borrowRecordMapper.update(record);

        // 更新图书状态
        book.setStatus(1); // 1: 可借阅
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.update(book);
    }

    @Override
    public List<BorrowRecord> findBorrowRecordsByUserId(Long userId) {
        return borrowRecordMapper.findByUserId(userId);
    }
} 