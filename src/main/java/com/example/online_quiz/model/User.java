package com.example.online_quiz.model;

public class User {
    private String username;
    private String password;
    private String role; // "ADMIN" or "STUDENT"
    private String branch; // BTech branch
    private String subject; // Admin subject

    public User() {}

    public User(String username, String password, String role, String branch, String subject) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.branch = branch;
        this.subject = subject;
    }
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.branch = "N/A";
        this.subject = "N/A";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
