package org.example.springboot.service;

import com.github.pagehelper.PageInfo;
import org.example.springboot.entity.Book;
import org.example.springboot.entity.BorrowRecord;

import java.util.List;

public interface BookService {
    Book findById(Long id);
    Book findByIsbn(String isbn);
    PageInfo<Book> findAll(int pageNum, int pageSize);
    PageInfo<Book> search(String keyword, int pageNum, int pageSize);
    void add(Book book);
    void update(Book book);
    void delete(Long id);
    void borrow(Long bookId, Long userId);
    void returnBook(Long bookId, Long userId);
    List<BorrowRecord> findBorrowRecordsByUserId(Long userId);
} 