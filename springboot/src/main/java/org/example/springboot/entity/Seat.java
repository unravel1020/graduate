package org.example.springboot.entity;

import lombok.Data;

@Data
public class Seat {
    private Long id;
    private String number;
    private String area;
    private String status;
    private String description;
} 