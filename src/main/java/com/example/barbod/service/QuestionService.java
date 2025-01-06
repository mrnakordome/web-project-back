// src/main/java/com/example/barbod/service/QuestionService.java
package com.example.barbod.service;

import com.example.barbod.model.Question;
import com.example.barbod.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Optional<Question> findQuestionById(String id) {
        return questionRepository.findById(id);
    }

    // Add more question-related methods if needed (e.g., findAll, delete, etc.)
}
