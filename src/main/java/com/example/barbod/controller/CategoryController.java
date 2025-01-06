package com.example.barbod.controller;

import com.example.barbod.model.Category;
import com.example.barbod.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*") // Adjust as needed for your environment
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= Existing Endpoints =================

    // ------------------- GET: Categories -------------------
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ------------------- POST: Add New Category -------------------
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody Map<String, String> request) {
        String name = request.get("name");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid category name"));
        }

        try {
            Category newCategory = new Category();
            newCategory.setName(name.trim());
            categoryRepository.save(newCategory);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Category added successfully!");
            response.put("category", newCategory);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Add Category Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }
}
