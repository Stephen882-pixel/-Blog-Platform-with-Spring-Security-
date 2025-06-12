package com.stephen.blog.services.impl;

import com.stephen.blog.domain.entities.Category;
import com.stephen.blog.repositories.CategoryRepository;
import com.stephen.blog.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> lisCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        String categoryName = category.getName();
        if(categoryRepository.existsByNameIgnoreCase(categoryName)){
            throw  new IllegalArgumentException("Category already exists with name: " +  categoryName);
        }
        return categoryRepository.save(category);
    }
}
