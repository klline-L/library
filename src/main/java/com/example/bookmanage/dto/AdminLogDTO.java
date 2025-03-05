package com.example.bookmanage.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminLogDTO {
    private Long id;              // 日志 ID
    private String adminUsername; // 管理员用户名
    private String actionType;    // 操作类型
    private String actionDetail;  // 操作详情
    private LocalDateTime actionTime; // 操作时间
}