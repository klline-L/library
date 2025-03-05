package com.example.bookmanage.service;

import com.example.bookmanage.entity.User;

import java.util.List;

public interface UserService {
    // 根据用户名查找用户（用于登录验证）
    User findByUsername(String username);

    // 用户注册
    void register(User user);

    // 用户登录验证
    User validateUser(String username, String password);

    // 根据 ID 获取用户信息
    User getUserById(Long id);

    // 更新用户信息
    void updateUser(User user);

    // 新增：获取所有用户列表
    List<User> getAllUsers();

    // 新增：删除用户
    void deleteUser(Long id);
}