package com.example.barbod.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "questions")
public class Question {

    @Id
    private String id;
    private String test;
    private Options options;
    private String correctAnswer;
    private String categoryId;
    private Integer difficulty;

    // ========== Constructors ==========

    public Question() {
        // default constructor
    }

    public Question(String id, String test, Options options, String correctAnswer,
                    String categoryId, Integer difficulty) {
        this.id = id;
        this.test = test;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.categoryId = categoryId;
        this.difficulty = difficulty;
    }

    // ========== Inner Class for Options ==========

    public static class Options {
        private String A;
        private String B;
        private String C;
        private String D;

        public Options() {
        }

        public Options(String A, String B, String C, String D) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.D = D;
        }

        public String getA() {
            return A;
        }

        public void setA(String A) {
            this.A = A;
        }

        public String getB() {
            return B;
        }

        public void setB(String B) {
            this.B = B;
        }

        public String getC() {
            return C;
        }

        public void setC(String C) {
            this.C = C;
        }

        public String getD() {
            return D;
        }

        public void setD(String D) {
            this.D = D;
        }
    }

    // ========== Getters and Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
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
}
