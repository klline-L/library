package com.example.bookmanage.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;                  // 用户 ID
    private String username;          // 用户名
    private String password;          // 密码
    private String role;              // 角色（USER 或 admin）
    private LocalDateTime registerDate; // 注册日期
    private String email;             // 邮箱
    private String gender;            // 性别
    private String avatar;            // 头像
    private LocalDate lastUsernameUpdate; // 上次用户名更新日期
}