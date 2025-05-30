package org.example.springboot.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;    // 用户名/学号
    private String password;
    private String realName;
    private String role;        // STUDENT/ADMIN
    private Integer status;     // 1:正常 0:禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 