package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Seat {
    private Long id;
    private String seatNumber;
    private String area;
    private String description;
    private Integer status;     // 1:可用 2:已预约
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 