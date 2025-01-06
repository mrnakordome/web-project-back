package com.example.barbod.controller;

public class SubmitAnswerRequest {
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
