package com.example.bookmanage.service.impl;

import com.example.bookmanage.dto.AdminLogDTO;
import com.example.bookmanage.entity.AdminLog;
import com.example.bookmanage.mapper.AdminLogMapper;
import com.example.bookmanage.mapper.UserMapper;
import com.example.bookmanage.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminLogServiceImpl implements AdminLogService {

    @Autowired
    private AdminLogMapper adminLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void logAction(Long adminId, String actionType, String actionDetail) {
        // 创建日志对象
        AdminLog log = new AdminLog();
        log.setAdminId(adminId);
        log.setActionType(actionType);
        log.setActionDetail(actionDetail);
        log.setActionTime(LocalDateTime.now());
        // 插入日志
        adminLogMapper.insert(log);
    }

    @Override
    public List<AdminLogDTO> getAllLogs() {
        // 获取所有日志并转换为 DTO，包括管理员用户名
        return adminLogMapper.findAll().stream().map(log -> {
            AdminLogDTO dto = new AdminLogDTO();
            dto.setId(log.getId());
            dto.setAdminUsername(userMapper.findById(log.getAdminId()).getUsername());
            dto.setActionType(log.getActionType());
            dto.setActionDetail(log.getActionDetail());
            dto.setActionTime(log.getActionTime());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteLog(Long id) {
        adminLogMapper.deleteById(id);
    }//删除单条

    @Override
    public void deleteLogs(List<Long> logIds) {
        if (logIds != null && !logIds.isEmpty()) {
            adminLogMapper.deleteByIds(logIds);
        }//删除多条
    }
}