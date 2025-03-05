package com.example.bookmanage.service;

import com.example.bookmanage.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCategories();
    Category findCategoryById(Long id);
}