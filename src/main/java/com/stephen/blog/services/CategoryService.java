package com.stephen.blog.services;

import com.stephen.blog.domain.entities.Category;

import java.util.List;

public interface CategoryService {
    List<Category> lisCategories();
    Category createCategory(Category category);
}
