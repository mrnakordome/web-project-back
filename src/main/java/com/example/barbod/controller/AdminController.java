package com.example.barbod.controller;

import com.example.barbod.dto.AddQuestionRequest;
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
    // ==================== POST /admin/questions ====================
    /**
     * Creates a new question by an admin.
     * Expects JSON body matching AddQuestionRequest.
     */
    @PostMapping("/questions")
    public ResponseEntity<?> addNewQuestion(@RequestBody AddQuestionRequest request) {
        // 1. Validate required fields
        if (request.getAdminId() == null || request.getTest() == null ||
                request.getOptions() == null || request.getCorrectAnswer() == null ||
                request.getCategoryId() == null || request.getDifficulty() == null) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "All fields are required."));
        }

        try {
            // 2. Fetch and validate admin
            Optional<User> adminOpt = userRepository.findById(request.getAdminId());
            if (adminOpt.isEmpty() || !"admin".equalsIgnoreCase(adminOpt.get().getRole())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Admin not found or invalid role."));
            }

            User admin = adminOpt.get();

            // 3. Fetch category by ID
            Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Category not found."));
            }
            Category category = categoryOpt.get();

            // 4. Convert AddQuestionRequest.OptionsDTO -> Question.Options
            AddQuestionRequest.OptionsDTO optionsDTO = request.getOptions();
            if (optionsDTO.getA() == null || optionsDTO.getB() == null ||
                    optionsDTO.getC() == null || optionsDTO.getD() == null) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "All options (A, B, C, D) are required."));
            }

            Question.Options questionOptions = new Question.Options(
                    optionsDTO.getA(),
                    optionsDTO.getB(),
                    optionsDTO.getC(),
                    optionsDTO.getD()
            );

            // 5. Create a new Question
            Question newQuestion = new Question();
            newQuestion.setTest(request.getTest());
            newQuestion.setOptions(questionOptions);
            newQuestion.setCorrectAnswer(request.getCorrectAnswer());
            newQuestion.setCategoryId(category.getId()); // Use the Category's ID
            newQuestion.setDifficulty(request.getDifficulty());

            // 6. Save the new question
            Question savedQuestion = questionRepository.save(newQuestion);

            // 7. Add the question ID to admin's "questions" list
            admin.getQuestions().add(savedQuestion.getId());
            userRepository.save(admin);

            // 8. Convert savedQuestion to QuestionResponseDTO
            QuestionResponseDTO questionDTO = new QuestionResponseDTO(
                    savedQuestion.getId(),
                    savedQuestion.getTest(),
                    savedQuestion.getOptions(),
                    savedQuestion.getCorrectAnswer(),
                    savedQuestion.getCategoryId(),
                    savedQuestion.getDifficulty()
            );

            // 9. Return success response with QuestionResponseDTO
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Question added successfully!");
            response.put("question", questionDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("Add Question Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }
    // ==================== GET /admin/{adminId}/questions ====================
    /**
     * Fetches all questions created by the admin with ID = adminId.
     * If the admin has created no questions, returns an empty list.
     */
    @GetMapping("/{adminId}/questions")
    public ResponseEntity<?> getAdminQuestions(@PathVariable("adminId") String adminId) {
        try {
            // 1. Validate admin existence
            Optional<User> adminOpt = userRepository.findById(adminId);
            if (adminOpt.isEmpty() || !"admin".equalsIgnoreCase(adminOpt.get().getRole())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Admin not found or invalid role."));
            }
            User admin = adminOpt.get();

            // 2. Retrieve question IDs from the admin
            List<String> questionIds = admin.getQuestions();
            if (questionIds == null || questionIds.isEmpty()) {
                // Admin has no questions
                return ResponseEntity.ok(Collections.emptyList());
            }

            // 3. Fetch the Question objects from questionIds
            List<Question> questions = questionRepository.findAllById(questionIds);

            // 4. Convert each Question to a simpler DTO
            List<QuestionResponseDTO> result = questions.stream()
                    .map(q -> new QuestionResponseDTO(
                            q.getId(),
                            q.getTest(),
                            q.getOptions(),
                            q.getCorrectAnswer(),
                            q.getCategoryId(),
                            q.getDifficulty()
                    ))
                    .collect(Collectors.toList());

            // 5. Return the list of questions as JSON
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Failed to fetch admin questions: " + e.getMessage());
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
    public static class QuestionResponseDTO {
        private String id;
        private String test;
        private Question.Options options;
        private String correctAnswer;
        private String categoryId;
        private Integer difficulty;

        public QuestionResponseDTO() {
        }

        public QuestionResponseDTO(String id, String test, Question.Options options,
                                   String correctAnswer, String categoryId, Integer difficulty) {
            this.id = id;
            this.test = test;
            this.options = options;
            this.correctAnswer = correctAnswer;
            this.categoryId = categoryId;
            this.difficulty = difficulty;
        }

        // Getters

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
