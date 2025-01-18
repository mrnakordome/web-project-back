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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*") // Adjust as needed for your environment
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // ================= Existing Endpoints =================

    // Existing GET /admin/{id} endpoint
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminDetails(@PathVariable String id) {
        Optional<User> adminOpt = userService.findAdminById(id);

        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();

            // Fetch questions
            List<QuestionDTO> questionDTOs = admin.getQuestions() != null
                    ? admin.getQuestions().stream()
                    .map(qId -> questionRepository.findById(qId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(q -> new QuestionDTO(
                            q.getId(),
                            q.getTest(),
                            q.getOptions(),
                            q.getCorrectAnswer(),
                            q.getCategoryId(),
                            q.getDifficulty()
                    ))
                    .collect(Collectors.toList())
                    : List.of();

            AdminDetailsResponse response = new AdminDetailsResponse(
                    admin.getId(),
                    admin.getUsername(),
                    admin.getAdminLevel(),
                    admin.getFollowingCount(),
                    admin.getFollowers() != null ? admin.getFollowers().size() : 0,
                    questionDTOs
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
    }

    // ================= New Endpoint =================
    @GetMapping("/username/{username}")
    public  ResponseEntity<?> getUserByUsername(@PathVariable("username") String username){
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if(userOpt.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Follower or following not found."));
            }
            User user = userOpt.get();
            if(Objects.equals(user.getRole(), "user")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Wrong role."));
            }

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.err.println("Error fetching User by username: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }
    // ------------------- POST: Add New Question -------------------
    @PostMapping("/questions")
    public ResponseEntity<?> addNewQuestion(@RequestBody Map<String, Object> request) {
        // Extract fields from the request body
        String adminId = (String) request.get("adminId");
        String test = (String) request.get("test");
        Map<String, String> optionsMap = (Map<String, String>) request.get("options");
        String correctAnswer = (String) request.get("correctAnswer");
        String categoryIdOrName = (String) request.get("categoryId");
        Integer difficulty = null;

        // Validate and parse difficulty
        try {
            difficulty = (Integer) request.get("difficulty");
            if (difficulty == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Difficulty must be an integer."));
            }
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Difficulty must be an integer."));
        }

        // Validate required fields
        if (adminId == null || test == null || optionsMap == null || correctAnswer == null
                || categoryIdOrName == null || difficulty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "All fields are required."));
        }

        try {
            // Fetch and validate admin
            Optional<User> adminOpt = userRepository.findById(adminId);
            if (adminOpt.isEmpty() || !"admin".equalsIgnoreCase(adminOpt.get().getRole())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Admin not found or invalid role."));
            }

            User admin = adminOpt.get();

            // Fetch category by ID or Name
            Optional<Category> categoryOpt = categoryRepository.findById(categoryIdOrName);
            if (categoryOpt.isEmpty()) {
                // If not found by ID, try finding by name
                categoryOpt = categoryRepository.findAll().stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(categoryIdOrName))
                        .findFirst();
                if (categoryOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Collections.singletonMap("error", "Category not found."));
                }
            }

            Category category = categoryOpt.get();

            // Create Options object
            Question.Options options = new Question.Options(
                    optionsMap.getOrDefault("A", ""),
                    optionsMap.getOrDefault("B", ""),
                    optionsMap.getOrDefault("C", ""),
                    optionsMap.getOrDefault("D", "")
            );

            // Create and save new question
            Question newQuestion = new Question();
            newQuestion.setTest(test);
            newQuestion.setOptions(options);
            newQuestion.setCorrectAnswer(correctAnswer);
            newQuestion.setCategoryId(category.getId()); // Use category's ID
            newQuestion.setDifficulty(difficulty);

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

    // Existing AdminDetailsResponse class
    static class AdminDetailsResponse {
        private String id;
        private String username;
        private Integer adminLevel;
        private Integer followin;
        private Integer followersCount;
        private List<QuestionDTO> questions;

        public AdminDetailsResponse() {
        }

        public AdminDetailsResponse(String id, String username, Integer adminLevel, Integer followin,
                                    Integer followersCount, List<QuestionDTO> questions) {
            this.id = id;
            this.username = username;
            this.adminLevel = adminLevel;
            this.followin = followin;
            this.followersCount = followersCount;
            this.questions = questions;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Integer getAdminLevel() {
            return adminLevel;
        }

        public Integer getFollowin() {
            return followin;
        }

        public Integer getFollowersCount() {
            return followersCount;
        }

        public List<QuestionDTO> getQuestions() {
            return questions;
        }
    }

    // Existing QuestionDTO class
    static class QuestionDTO {
        private String id;
        private String test;
        private Question.Options options;
        private String correctAnswer;
        private String categoryId;
        private Integer difficulty;

        public QuestionDTO() {
        }

        public QuestionDTO(String id, String test, Question.Options options, String correctAnswer,
                           String categoryId, Integer difficulty) {
            this.id = id;
            this.test = test;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.categoryId = categoryId;
            this.difficulty = difficulty;
        }

        public String getId() {
            return id;
        }

        public String getTest() {
            return test;
        }

        public Question.Options getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public Integer getDifficulty() {
            return difficulty;
        }
    }
}
