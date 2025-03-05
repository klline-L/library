package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminLogMapper {
    // 插入日志记录
    void insert(AdminLog log);

    // 查询所有日志记录
    List<AdminLog> findAll();

    // 根据管理员 ID 查询日志
    List<AdminLog> findByAdminId(@Param("adminId") Long adminId);

    // 删除指定日志
    void deleteById(@Param("id") Long id);

    //批量删除日志
    void deleteByIds(@Param("ids") List<Long> ids);

}