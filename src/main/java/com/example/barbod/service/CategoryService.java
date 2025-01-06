// src/main/java/com/example/barbod/service/CategoryService.java
package com.example.barbod.service;

import com.example.barbod.model.Category;
import com.example.barbod.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> findCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    // Add more category-related methods if needed
}
