package com.example.bookmanage.service.impl;

import com.example.bookmanage.entity.Category;
import com.example.bookmanage.mapper.CategoryMapper;
import com.example.bookmanage.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findAllCategories() {
        return categoryMapper.findAll();
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryMapper.findById(id);
    }
}