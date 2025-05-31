package org.example.springboot.controller;

import com.github.pagehelper.PageInfo;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Book;
import org.example.springboot.entity.BorrowRecord;
import org.example.springboot.service.BookService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "图书管理", description = "图书相关的所有接口")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "获取图书列表", description = "分页获取所有图书信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取图书列表"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "403", description = "禁止访问"),
        @ApiResponse(responseCode = "404", description = "资源不存在")
    })
    @GetMapping
    public Result<PageInfo<Book>> findAll(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bookService.findAll(pageNum, pageSize));
    }

    @Operation(summary = "获取图书详情", description = "根据ID获取图书详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取图书详情"),
        @ApiResponse(responseCode = "404", description = "图书不存在")
    })
    @GetMapping("/{id}")
    public Result<Book> findById(@PathVariable Long id) {
        return Result.success(bookService.findById(id));
    }

    @GetMapping("/isbn/{isbn}")
    public Result<Book> findByIsbn(@PathVariable String isbn) {
        return Result.success(bookService.findByIsbn(isbn));
    }

    @GetMapping("/search")
    public Result<PageInfo<Book>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bookService.search(keyword, pageNum, pageSize));
    }

    @Operation(summary = "添加新图书", description = "添加一本新的图书到系统")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "成功创建图书"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping
    public Result<Void> add(@RequestBody Book book) {
        bookService.add(book);
        return Result.success();
    }

    @Operation(summary = "更新图书信息", description = "更新指定ID的图书信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新图书信息"),
        @ApiResponse(responseCode = "404", description = "图书不存在")
    })
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.update(book);
        return Result.success();
    }

    @Operation(summary = "删除图书", description = "删除指定ID的图书")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功删除图书"),
        @ApiResponse(responseCode = "404", description = "图书不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/borrow")
    public Result<Void> borrow(@PathVariable Long id) {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        bookService.borrow(id, userId);
        return Result.success();
    }

    @PostMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        bookService.returnBook(id, userId);
        return Result.success();
    }

    @GetMapping("/borrow-records")
    public Result<List<BorrowRecord>> findBorrowRecords() {
        // 从SecurityContext中获取当前用户ID
        Long userId = 1L; // TODO: 从SecurityContext中获取
        return Result.success(bookService.findBorrowRecordsByUserId(userId));
    }
} 