package com.example.bookmanage.entity;

import lombok.Data;

@Data
public class Settings {
    private Long id;              // 设置 ID
    private String announcement;  // 系统公告
    private String excerpt;       // 系统简介
    private Integer borrowDays;   // 借阅期限（天）
    private Double finePerDay;    // 逾期罚款（元/天）
    private Boolean enableNotifications; // 是否启用通知
}