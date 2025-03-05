package com.example.bookmanage.service;

import com.example.bookmanage.dto.SettingsDTO;
import com.example.bookmanage.entity.Settings;

public interface SettingsService {
    // 获取系统公告
    String getAnnouncement();

    // 获取系统简介
    String getExcerpt();

    // 获取当前系统设置
    Settings getSettings();

    // 更新系统设置
    void updateSettings(SettingsDTO settingsDTO);
}