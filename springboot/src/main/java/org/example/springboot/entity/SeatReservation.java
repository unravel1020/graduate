package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeatReservation {
    private Long id;
    private Long seatId;
    private Long userId;
    private LocalDateTime reserveTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;  // pending, confirmed, cancelled, completed
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 