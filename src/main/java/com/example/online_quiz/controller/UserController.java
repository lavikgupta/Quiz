package com.example.online_quiz.controller;

import com.example.online_quiz.model.User;
import com.example.online_quiz.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private InMemoryStorage storage;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.get("role");
        String branch = payload.get("branch");
        String subject = payload.get("subject");
        String adminPassword = payload.get("adminPassword");

        if (storage.getUserByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Username already exists"));
        }
        if (role == null || role.isEmpty()) {
            role = "STUDENT"; // Default role
        }
        if ("ADMIN".equals(role)) {
            if (!"Csjmu1234".equals(adminPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Admin Password"));
            }
        }
        
        User user = new User(username, password, role, branch, subject);
        storage.saveUser(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        User user = storage.getUserByUsername(loginRequest.getUsername());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
    }
}
