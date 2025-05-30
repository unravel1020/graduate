package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowRecord {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status;    // 1:借阅中 2:已归还 3:逾期
    private LocalDateTime createTime;
} 