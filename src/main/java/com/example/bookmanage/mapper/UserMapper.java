package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    // 查询所有用户
    List<User> findAll();

    // 根据 ID 查询用户
    User findById(Long id);

    // 根据用户名查询用户（用于登录验证）
    User findByUsername(String username);

    // 插入新用户
    void insert(User user);

    // 更新用户信息
    void update(User user);

    // 删除用户
    void delete(Long id);

    // 分页查询用户
    List<User> findUsersByPage(@Param("offset") int offset, @Param("limit") int limit);

}