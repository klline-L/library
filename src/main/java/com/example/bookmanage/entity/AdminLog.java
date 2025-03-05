package com.example.bookmanage.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminLog {
    private Long id;              // 日志 ID
    private Long adminId;         // 执行操作的管理员 ID
    private String actionType;    // 操作类型（如 DELETE_BOOKS）
    private String actionDetail;  // 操作详情
    private LocalDateTime actionTime; // 操作时间
}

