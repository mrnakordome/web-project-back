package com.example.barbod.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.barbod.model.Question;
import com.example.barbod.model.User;
import com.example.barbod.repository.QuestionRepository;
import com.example.barbod.repository.UserRepository;
import com.example.barbod.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Printable;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*") // Adjust as needed for your environment
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // ================= Existing Endpoints =================

    // Existing GET /user/{id} endpoint
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable String id) {
        Optional<User> userOpt = userService.findUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            UserDetailsResponse response = new UserDetailsResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getFollowers() != null ? user.getFollowers().size() : 0,
                    user.getFollowingCount()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // ================= New Endpoints =================
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody FollowRequest request){
        System.out.println(request.toString());
        try {
            Optional<User> followerOpt = userRepository.findById(request.getFollowerId());
            Optional<User> followingOpt = userRepository.findById(request.getFollowingId());

            if(followerOpt.isEmpty() || followingOpt.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Follower or following not found."));
            }
            User follower = followerOpt.get();
            User following = followingOpt.get();

            //List of users followed by the "follower" user
            List<String> followed = follower.getFollowings();
            if(followed == null) followed = new ArrayList<>();

            followed.add(following.getId());
            follower.setFollowings(followed);

            //List of users following the "following" user
            List<String> followers = following.getFollowers();
            if(followers == null) followers = new ArrayList<>();

            followers.add(follower.getId());
            following.setFollowers(followers);

            userRepository.save(follower);
            userRepository.save(following);

            System.out.println(request.getFollowerId() + " now follows " + request.getFollowingId());
            return ResponseEntity.ok(Collections.singletonMap("message", "You are now following " + following.getUsername()));
        } catch (Exception e) {
            System.err.println("Error following user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ------------------- GET: Leaderboard -------------------
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        System.out.println("Getting Leaderboard");
        try {
            // Exclude admins and sort by points descending
            List<User> userList = userRepository.findByRoleNot("admin", Sort.by(Sort.Direction.DESC, "points"));

            System.out.println(userList);

            // Transform userList into a leaderboard list
            List<Map<String, Object>> leaderboard = userList.stream().map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("username", user.getUsername());
                map.put("points", user.getPoints());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Collections.singletonMap("leaderboard", leaderboard));
        } catch (Exception e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ------------------- GET: Random Unanswered Question -------------------
    @GetMapping("/{userId}/questions/random")
    public ResponseEntity<?> getRandomUnansweredQuestion(@PathVariable("userId") String userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            User user = userOpt.get();

            // Collect already answered question IDs
            List<String> answeredIds = user.getAnsweredQuestions()
                    .stream()
                    .map(User.AnsweredQuestion::getQuestionId)
                    .collect(Collectors.toList());

            // Fetch questions not in answeredIds
            List<Question> unansweredQuestions = questionRepository.findByIdNotIn(answeredIds);

            if (unansweredQuestions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No unanswered questions available."));
            }

            // Pick a random question
            Random random = new Random();
            Question randomQuestion = unansweredQuestions.get(random.nextInt(unansweredQuestions.size()));

            return ResponseEntity.ok(randomQuestion);
        } catch (Exception e) {
            System.err.println("Error fetching random question: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }
    // ------------------- GET: User by username -------------------
    @GetMapping("/username/{username}")
    public  ResponseEntity<?> getUserByUsername(@PathVariable("username") String username){
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            return ResponseEntity.ok(userOpt);
        } catch (Exception e) {
            System.err.println("Error fetching User by username: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ------------------- GET: Unanswered Question by Category -------------------
    @GetMapping("/{id}/questions/category/{categoryId}")
    public ResponseEntity<?> getUnansweredQuestionByCategory(@PathVariable("id") String userId,
                                                             @PathVariable("categoryId") String categoryId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            User user = userOpt.get();

            // Collect already answered question IDs
            List<String> answeredIds = user.getAnsweredQuestions()
                    .stream()
                    .map(User.AnsweredQuestion::getQuestionId)
                    .collect(Collectors.toList());

            // Fetch questions in the specified category and not answered by the user
            List<Question> unansweredQuestions = questionRepository.findByCategoryIdAndIdNotIn(categoryId, answeredIds);

            if (unansweredQuestions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No unanswered questions available in this category."));
            }

            // Pick a random question
            Random random = new Random();
            Question randomQuestion = unansweredQuestions.get(random.nextInt(unansweredQuestions.size()));

            return ResponseEntity.ok(randomQuestion);
        } catch (Exception e) {
            System.err.println("Error fetching random question from category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }

    // ------------------- POST: Submit Answer -------------------
    @PostMapping("/{userId}/questions/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable("userId") String userId,
                                          @RequestBody SubmitAnswerRequest request) {
        try {
            String questionId = request.getQuestionId();
            String userAnswer = request.getUserAnswer();

            // Log the incoming request
            System.out.println("Submitting answer for User ID: " + userId);
            System.out.println("Question ID: " + questionId);
            System.out.println("User Answer: " + userAnswer);

            // 1. Validate request body
            if (questionId == null || userAnswer == null) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "questionId and userAnswer are required."));
            }

            // 2. Fetch user & question
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Question> questionOpt = questionRepository.findById(questionId);

            if (userOpt.isEmpty()) {
                System.out.println("User not found: " + userId);
            }
            if (questionOpt.isEmpty()) {
                System.out.println("Question not found: " + questionId);
            }
            if (userOpt.isEmpty() || questionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "User or question not found."));
            }

            User user = userOpt.get();
            Question question = questionOpt.get();

            // 3. Check if question is already answered
            boolean alreadyAnswered = user.getAnsweredQuestions().stream()
                    .anyMatch(aq -> aq.getQuestionId().equals(questionId));
            if (alreadyAnswered) {
                System.out.println("Question already answered by user: " + questionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Question already answered."));
            }

            // 4. Record answered question
            User.AnsweredQuestion answered = new User.AnsweredQuestion(questionId, userAnswer);
            user.getAnsweredQuestions().add(answered);

            // 5. Check correctness
            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
            if (isCorrect) {
                user.setPoints(user.getPoints() + question.getDifficulty());
                System.out.println("Answer is correct. Updated points: " + user.getPoints());
            } else {
                System.out.println("Answer is incorrect.");
            }
            userRepository.save(user);

            // 6. Send success response
            String message = isCorrect ? "Correct answer!" : "Wrong answer!";
            return ResponseEntity.ok(Collections.singletonMap("message", message));

        } catch (Exception e) {
            System.err.println("Submit Answer Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Server error"));
        }
    }


    // ================= Inner DTO Classes =================

    // Existing UserDetailsResponse class
    static class UserDetailsResponse {
        private String id;
        private String username;
        private Integer followersCount;
        private Integer followingCount;

        public UserDetailsResponse() {
        }

        public UserDetailsResponse(String id, String username, Integer followersCount, Integer followingCount) {
            this.id = id;
            this.username = username;
            this.followersCount = followersCount;
            this.followingCount = followingCount;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Integer getFollowersCount() {
            return followersCount;
        }

        public Integer getFollowingCount() {
            return followingCount;
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

    // New DTO for Submit Answer Request
    static class SubmitAnswerRequest {
        private String questionId;
        private String userAnswer;

        public SubmitAnswerRequest() {
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }
    }
    static class FollowRequest {
        @JsonProperty("followerId")
        private String followerId;

        @JsonProperty("followingId")
        private String followingId;

        @JsonProperty("role")
        private String role;

        // Getters and Setters
        public String getFollowerId() {
            return followerId;
        }

        public void setFollowerId(String followerId) {
            this.followerId = followerId;
        }

        public String getFollowingId() {
            return followingId;
        }

        public void setFollowingId(String followingId) {
            this.followingId = followingId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            return "FollowRequest{" +
                    "followerId='" + followerId + '\'' +
                    ", followingId='" + followingId + '\'' +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
}
