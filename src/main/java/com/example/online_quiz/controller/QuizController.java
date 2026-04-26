package com.example.online_quiz.controller;

import com.example.online_quiz.model.Question;
import com.example.online_quiz.model.QuizResult;
import com.example.online_quiz.repository.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private InMemoryStorage storage;

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String branch = (String) payload.get("branch");
        String subject = (String) payload.get("subject");
        Map<String, String> answers = (Map<String, String>) payload.get("answers"); // questionId -> selectedOption

        if (username == null || answers == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid payload"));
        }

        List<Question> quizQuestions;
        if (branch != null && subject != null) {
            quizQuestions = storage.getQuestionsByBranchAndSubject(branch, subject);
        } else {
            quizQuestions = storage.getAllQuestions();
            subject = "General"; // Default fallback
        }
        
        int score = 0;
        int totalQuestions = quizQuestions.size();

        for (Question q : quizQuestions) {
            String selectedOption = answers.get(q.getId());
            if (selectedOption != null && selectedOption.equals(q.getCorrectOption())) {
                score++;
            }
        }

        QuizResult result = new QuizResult(username, score, totalQuestions, subject);
        storage.saveResult(result);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/results/{username}")
    public ResponseEntity<?> getResults(@PathVariable String username) {
        List<QuizResult> results = storage.getResultsByUsername(username);
        return ResponseEntity.ok(results);
    }
}
