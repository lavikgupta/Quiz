package com.example.online_quiz.model;

public class QuizResult {
    private String username;
    private int score;
    private int totalQuestions;
    private String subject;

    public QuizResult() {}

    public QuizResult(String username, int score, int totalQuestions, String subject) {
        this.username = username;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.subject = subject;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
