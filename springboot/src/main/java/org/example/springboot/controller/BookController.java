package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Book;
import org.example.springboot.entity.BorrowRecord;
import org.example.springboot.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public Result<Book> findById(@PathVariable Long id) {
        return Result.success(bookService.findById(id));
    }

    @GetMapping("/isbn/{isbn}")
    public Result<Book> findByIsbn(@PathVariable String isbn) {
        return Result.success(bookService.findByIsbn(isbn));
    }

    @GetMapping
    public Result<PageInfo<Book>> findAll(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bookService.findAll(pageNum, pageSize));
    }

    @GetMapping("/search")
    public Result<PageInfo<Book>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bookService.search(keyword, pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> add(@RequestBody Book book) {
        bookService.add(book);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.update(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/borrow")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Void> borrow(@PathVariable Long id) {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        bookService.borrow(id, userId);
        return Result.success();
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Void> returnBook(@PathVariable Long id) {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        bookService.returnBook(id, userId);
        return Result.success();
    }

    @GetMapping("/borrow-records")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<BorrowRecord>> findBorrowRecords() {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        return Result.success(bookService.findBorrowRecordsByUserId(userId));
    }
} 