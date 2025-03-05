package com.example.bookmanage.service;

import com.example.bookmanage.dto.AdminLogDTO;
import java.util.List;

public interface AdminLogService {
    // 记录管理员操作日志
    void logAction(Long adminId, String actionType, String actionDetail);

    // 获取所有日志列表
    List<AdminLogDTO> getAllLogs();

    // 删除指定日志
    void deleteLog(Long id);

    //批量删除日志
    void deleteLogs(List<Long> logIds);

}