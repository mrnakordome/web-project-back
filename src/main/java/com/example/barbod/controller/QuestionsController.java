package com.example.barbod.controller;

import com.example.barbod.model.Category;
import com.example.barbod.model.Question;
import com.example.barbod.model.User;
import com.example.barbod.repository.CategoryRepository;
import com.example.barbod.repository.QuestionRepository;
import com.example.barbod.repository.UserRepository;
import com.example.barbod.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "*") // Adjust as needed for your environment
public class QuestionsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= New Endpoint =================

    // ------------------- POST: Add New Question -------------------
    @PostMapping
    public ResponseEntity<?> addNewQuestion(@RequestBody AddQuestionRequest request) {
        // Validate required fields
        if (request.getAdminId() == null || request.getTest() == null ||
                request.getOptions() == null || request.getCorrectAnswer() == null ||
                request.getCategoryId() == null || request.getDifficulty() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "All fields are required."));
        }

        try {
            // Fetch and validate admin
            Optional<User> adminOpt = userRepository.findById(request.getAdminId());
            if (adminOpt.isEmpty() || !"admin".equalsIgnoreCase(adminOpt.get().getRole())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Admin not found or invalid role."));
            }

            User admin = adminOpt.get();

            // Fetch category by ID
            Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Category not found."));
            }

            Category category = categoryOpt.get();

            // Create Options object
            Question.Options options = new Question.Options(
                    request.getOptions().getA(),
                    request.getOptions().getB(),
                    request.getOptions().getC(),
                    request.getOptions().getD()
            );

            // Create and save new question
            Question newQuestion = new Question();
            newQuestion.setTest(request.getTest());
            newQuestion.setOptions(options);
            newQuestion.setCorrectAnswer(request.getCorrectAnswer());
            newQuestion.setCategoryId(category.getId()); // Use category's ID
            newQuestion.setDifficulty(request.getDifficulty());

            Question savedQuestion = questionRepository.save(newQuestion);

            // Add question ID to admin's questions list
            admin.getQuestions().add(savedQuestion.getId());
            userRepository.save(admin);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Question added successfully!");
            response.put("question", savedQuestion);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Add Question Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ================= Inner DTO Classes =================

    // DTO for Add Question Request
    public static class AddQuestionRequest {
        private String adminId;
        private String test;
        private OptionsDTO options;
        private String correctAnswer;
        private String categoryId;
        private Integer difficulty;

        // Getters and Setters

        public String getAdminId() {
            return adminId;
        }

        public void setAdminId(String adminId) {
            this.adminId = adminId;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public OptionsDTO getOptions() {
            return options;
        }

        public void setOptions(OptionsDTO options) {
            this.options = options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public Integer getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(Integer difficulty) {
            this.difficulty = difficulty;
        }

        // Inner DTO for Options
        public static class OptionsDTO {
            private String A;
            private String B;
            private String C;
            private String D;

            public OptionsDTO() {
            }

            public String getA() {
                return A;
            }

            public void setA(String a) {
                A = a;
            }

            public String getB() {
                return B;
            }

            public void setB(String b) {
                B = b;
            }

            public String getC() {
                return C;
            }

            public void setC(String c) {
                C = c;
            }

            public String getD() {
                return D;
            }

            public void setD(String d) {
                D = d;
            }
        }
    }
}
