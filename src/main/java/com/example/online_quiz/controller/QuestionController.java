package com.example.online_quiz.controller;

import com.example.online_quiz.model.Question;
import com.example.online_quiz.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private InMemoryStorage storage;

    @GetMapping
    public List<Question> getAllQuestions(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String subject) {
        if (branch != null || subject != null) {
            return storage.getQuestionsByBranchAndSubject(branch, subject);
        }
        return storage.getAllQuestions();
    }

    @GetMapping("/subjects")
    public List<String> getSubjects(@RequestParam(required = false) String branch) {
        return storage.getSubjectsByBranch(branch);
    }

    @PostMapping
    public ResponseEntity<?> addQuestion(@RequestBody Question question) {
        if (question.getId() == null || question.getId().isEmpty()) {
            question.setId(UUID.randomUUID().toString());
        }
        storage.saveQuestion(question);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable String id, @RequestBody Question question) {
        Question existing = storage.getQuestionById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        question.setId(id); // Ensure ID remains the same
        storage.updateQuestion(id, question);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        Question existing = storage.getQuestionById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        storage.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }
}
