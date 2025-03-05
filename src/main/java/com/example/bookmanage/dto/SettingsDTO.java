package com.example.bookmanage.dto;

import lombok.Data;

@Data
public class SettingsDTO {
    private Integer borrowDays;   // 借阅期限（天）
    private Double finePerDay;    // 逾期罚款（元/天）
    private Boolean enableNotifications; // 是否启用通知
}