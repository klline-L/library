package com.example.bookmanage.service.impl;

import com.example.bookmanage.entity.User;
import com.example.bookmanage.mapper.UserMapper;
import com.example.bookmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        // 根据用户名查找用户
        return userMapper.findByUsername(username);
    }

    @Override
    public void register(User user) {
        // 用户注册，加密密码并设置默认角色
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); // 默认角色为普通用户
        userMapper.insert(user);
    }

    @Override
    public User validateUser(String username, String password) {
        // 用户登录验证，检查用户名和密码是否匹配
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user; // 用户名和密码匹配，返回用户对象
        }
        return null; // 验证失败，返回 null
    }

    @Override
    public User getUserById(Long id) {
        // 根据 ID 获取用户信息
        return userMapper.findById(id);
    }

    @Override
    public void updateUser(User user) {
        // 更新用户信息
        userMapper.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        // 获取所有用户列表
        return userMapper.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        // 删除用户
        userMapper.delete(id);
    }
}

