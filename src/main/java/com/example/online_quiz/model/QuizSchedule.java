package com.example.online_quiz.model;

import java.time.LocalDateTime;

public class QuizSchedule {
    private String branch;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public QuizSchedule() {}

    public QuizSchedule(String branch, String subject, LocalDateTime startTime, LocalDateTime endTime) {
        this.branch = branch;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
