package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String description;
    private String coverUrl;
    private Integer status; // 1: 可借阅, 2: 已借出
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 