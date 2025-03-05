package com.example.bookmanage.service.impl;

import com.example.bookmanage.dto.SettingsDTO;
import com.example.bookmanage.entity.Settings;
import com.example.bookmanage.mapper.SettingsMapper;
import com.example.bookmanage.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public String getAnnouncement() {
        // 获取系统公告
        Settings settings = settingsMapper.findSettings();
        return settings != null ? settings.getAnnouncement() : "";
    }

    @Override
    public String getExcerpt() {
        // 获取系统简介
        Settings settings = settingsMapper.findSettings();
        return settings != null ? settings.getExcerpt() : "";
    }

    @Override
    public Settings getSettings() {
        // 获取当前系统设置
        Settings settings = settingsMapper.findSettings();
        if (settings == null) {
            settings = new Settings();
            settings.setId(1L); // 假设只有一个设置记录，ID 为 1
            settings.setBorrowDays(14);
            settings.setFinePerDay(0.50);
            settings.setEnableNotifications(true);
        }
        return settings;
    }

    @Override
    public void updateSettings(SettingsDTO settingsDTO) {
        // 更新系统设置
        Settings settings = getSettings();
        settings.setBorrowDays(settingsDTO.getBorrowDays());
        settings.setFinePerDay(settingsDTO.getFinePerDay());
        settings.setEnableNotifications(settingsDTO.getEnableNotifications());
        settingsMapper.update(settings);
    }
}