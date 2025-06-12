package com.stephen.blog.controllers;


import com.stephen.blog.domain.entities.Category;
import com.stephen.blog.domain.entities.Post;
import com.stephen.blog.dtos.CategoryDto;
import com.stephen.blog.dtos.CreateCategoryRequest;
import com.stephen.blog.mappers.CategoryMapper;
import com.stephen.blog.repositories.CategoryRepository;
import com.stephen.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor

public class CategoryController {

    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories(){
        List<CategoryDto> categories = categoryService.lisCategories()
                .stream().map(categoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryDto>  createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest){
         Category categoryToCreate =  categoryMapper.toEntity(createCategoryRequest);
         Category savedCategory = categoryService.createCategory(categoryToCreate);
         return new ResponseEntity<>(
                 categoryMapper.toDto(savedCategory),
                 HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
