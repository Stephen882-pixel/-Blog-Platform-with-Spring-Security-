package com.stephen.blog.services;

import com.stephen.blog.domain.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> lisCategories();
    Category createCategory(Category category);
    void deleteCategory(UUID id);
}
