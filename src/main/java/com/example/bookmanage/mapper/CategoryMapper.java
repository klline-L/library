package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAll();
    Category findById(Long id);
}