package com.example.barbod.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String role; // "user" or "admin"
    private Integer adminLevel;
    private List<String> followings;
    private List<String> followers; // List of User IDs
    private List<String> questions; // List of Question IDs
    private Integer points;
    private List<AnsweredQuestion> answeredQuestions;

    // ========== Constructors ==========

    public User() {
        // default constructor
    }

    public User(String id, String username, String password, String role, Integer adminLevel,
                List<String> followings, List<String> followers, List<String> questions, Integer points,
                List<AnsweredQuestion> answeredQuestions) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.adminLevel = adminLevel;
        this.followings = followings;
        this.followers = followers;
        this.questions = questions;
        this.points = points;
        this.answeredQuestions = answeredQuestions;
    }

    // ========== Inner Class for AnsweredQuestion ==========

    public static class AnsweredQuestion {
        private String questionId;
        private String answer;

        public AnsweredQuestion() {
        }

        public AnsweredQuestion(String questionId, String answer) {
            this.questionId = questionId;
            this.answer = answer;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    // ========== Getters and Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(Integer adminLevel) {
        this.adminLevel = adminLevel;
    }

    public Integer getFollowingCount() {
        return followings.size();
    }
    public List<String> getFollowings(){
        return followings;
    }
    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }

    public List<String> getFollowers() {
        if (this.followers == null) {
            this.followers = new ArrayList<>();
        }
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getQuestions() {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public List<AnsweredQuestion> getAnsweredQuestions() {
        if (this.answeredQuestions == null) {
            this.answeredQuestions = new ArrayList<>();
        }
        return answeredQuestions;
    }

    public void setAnsweredQuestions(List<AnsweredQuestion> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }
}
