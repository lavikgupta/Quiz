package com.example.online_quiz.controller;

import com.example.online_quiz.model.QuizSchedule;
import com.example.online_quiz.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/schedule")
public class QuizScheduleController {

    @Autowired
    private InMemoryStorage storage;

    @GetMapping
    public ResponseEntity<?> getSchedule(@RequestParam String branch, @RequestParam String subject) {
        QuizSchedule schedule = storage.getSchedule(branch, subject);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @PostMapping
    public ResponseEntity<?> saveSchedule(@RequestBody QuizSchedule schedule) {
        if (schedule.getBranch() == null || schedule.getSubject() == null) {
            return ResponseEntity.badRequest().body("Branch and subject are required");
        }
        storage.saveSchedule(schedule);
        return ResponseEntity.ok(schedule);
    }
}
