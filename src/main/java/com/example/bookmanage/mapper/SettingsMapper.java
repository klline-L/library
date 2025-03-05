package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.Settings;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SettingsMapper {
    // 根据 ID 查询设置
    Settings findById(Long id);

    // 更新设置
    void update(Settings settings);

    // 获取当前系统设置（假设只有一个记录）
    Settings findSettings();
}