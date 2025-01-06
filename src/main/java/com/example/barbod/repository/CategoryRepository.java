// src/main/java/com/example/barbod/repository/CategoryRepository.java
package com.example.barbod.repository;

import com.example.barbod.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    // Additional query methods if needed
}
