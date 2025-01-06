// src/main/java/com/example/barbod/model/Category.java
package com.example.barbod.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {

    @Id
    private String id;
    private String name;

    // ========== Constructors ==========

    public Category() {
        // default constructor
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ========== Getters and Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
