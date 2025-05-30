package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeatReservation {
    private Long id;
    private Long userId;
    private Long seatId;
    private LocalDateTime reservationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;    // 1:预约中 2:使用中 3:已结束
    private LocalDateTime createTime;
} 