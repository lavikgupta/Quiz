package com.example.online_quiz.repository;

import com.example.online_quiz.model.Question;
import com.example.online_quiz.model.QuizResult;
import com.example.online_quiz.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.online_quiz.model.QuizSchedule;

@Repository
public class InMemoryStorage {
    private Map<String, User> users = new HashMap<>();
    private List<Question> questions = new ArrayList<>();
    private List<QuizResult> results = new ArrayList<>();
    private Map<String, QuizSchedule> schedules = new HashMap<>();

    public InMemoryStorage() {
        // Add default admin users for different branches/subjects
        users.put("admin", new User("admin", "admin123", "ADMIN", "CSE", "Java"));
        users.put("admin_mech", new User("admin_mech", "admin123", "ADMIN", "ME", "Thermodynamics"));

        // Add CSE Java Questions
        questions.add(new Question("cse1", "What is Java?", "A programming language", "A coffee", "An OS", "A browser", "A", "CSE", "Java"));
        questions.add(new Question("cse2", "What is OOP?", "Object Oriented Programming", "Out of Place", "Over Object Pattern", "None", "A", "CSE", "Java"));
        questions.add(new Question("cse3", "Which keyword is used to inherit a class?", "extends", "implements", "inherits", "super", "A", "CSE", "Java"));
        questions.add(new Question("cse4", "What is the size of int in Java?", "2 bytes", "4 bytes", "8 bytes", "Depends on OS", "B", "CSE", "Java"));
        questions.add(new Question("cse5", "Which of these is not a Java feature?", "Dynamic", "Architecture Neutral", "Use of pointers", "Object-oriented", "C", "CSE", "Java"));

        // Add ME Thermodynamics Questions
        questions.add(new Question("me1", "What is the unit of energy?", "Joule", "Newton", "Pascal", "Watt", "A", "ME", "Thermodynamics"));
        questions.add(new Question("me2", "First law of thermodynamics relates to?", "Conservation of momentum", "Conservation of mass", "Conservation of energy", "Entropy", "C", "ME", "Thermodynamics"));
        questions.add(new Question("me3", "An adiabatic process is one in which?", "Pressure is constant", "Volume is constant", "Temperature is constant", "No heat enters or leaves", "D", "ME", "Thermodynamics"));
        questions.add(new Question("me4", "What does entropy measure?", "Energy", "Work", "Disorder", "Temperature", "C", "ME", "Thermodynamics"));
        questions.add(new Question("me5", "The efficiency of a Carnot engine depends on?", "Source temperature only", "Sink temperature only", "Both source and sink temperatures", "Working substance", "C", "ME", "Thermodynamics"));

        // Add ECE Digital Electronics Questions
        questions.add(new Question("ece1", "Which gate is called Universal Gate?", "AND", "OR", "NAND", "NOT", "C", "ECE", "Digital Electronics"));
        questions.add(new Question("ece2", "Base of binary number system is?", "2", "8", "10", "16", "A", "ECE", "Digital Electronics"));
        questions.add(new Question("ece3", "A flip-flop stores?", "1 bit", "2 bits", "1 byte", "None", "A", "ECE", "Digital Electronics"));
        questions.add(new Question("ece4", "Boolean algebra handles?", "Continuous values", "Discrete values", "Only 0s and 1s", "Real numbers", "C", "ECE", "Digital Electronics"));
        questions.add(new Question("ece5", "How many bits in a byte?", "4", "8", "16", "32", "B", "ECE", "Digital Electronics"));

        // Add CE Fluid Mechanics Questions
        questions.add(new Question("ce1", "Density of water is?", "100 kg/m3", "1000 kg/m3", "10 kg/m3", "1 kg/m3", "B", "CE", "Fluid Mechanics"));
        questions.add(new Question("ce2", "Viscosity is the property of fluid by virtue of which it offers resistance to?", "Flow", "Pressure", "Temperature", "Density", "A", "CE", "Fluid Mechanics"));
        questions.add(new Question("ce3", "Bernoulli's equation is based on conservation of?", "Mass", "Momentum", "Energy", "Force", "C", "CE", "Fluid Mechanics"));
        questions.add(new Question("ce4", "Mach number is the ratio of inertia force to?", "Viscous force", "Gravity force", "Elastic force", "Surface tension force", "C", "CE", "Fluid Mechanics"));
        questions.add(new Question("ce5", "Hydraulic jump occurs when flow changes from?", "Super-critical to sub-critical", "Sub-critical to super-critical", "Laminar to turbulent", "None", "A", "CE", "Fluid Mechanics"));

        // Add EE Circuit Theory Questions
        questions.add(new Question("ee1", "Ohm's law is valid only when?", "Temperature is constant", "Current is constant", "Voltage is constant", "None", "A", "EE", "Circuit Theory"));
        questions.add(new Question("ee2", "Unit of resistance is?", "Volt", "Ampere", "Ohm", "Watt", "C", "EE", "Circuit Theory"));
        questions.add(new Question("ee3", "Kirchhoff's current law is based on conservation of?", "Energy", "Momentum", "Charge", "Mass", "C", "EE", "Circuit Theory"));
        questions.add(new Question("ee4", "Power factor of a pure resistive circuit is?", "0", "0.5", "1", "Infinity", "C", "EE", "Circuit Theory"));
        questions.add(new Question("ee5", "Capacitor stores energy in form of?", "Magnetic field", "Electric field", "Heat", "Light", "B", "EE", "Circuit Theory"));
    }

    // User operations
    public void saveUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    // Question operations
    public List<Question> getAllQuestions() {
        return questions;
    }

    public List<Question> getQuestionsByBranchAndSubject(String branch, String subject) {
        return questions.stream()
                .filter(q -> (branch == null || q.getBranch().equals(branch)) &&
                             (subject == null || q.getSubject().equals(subject)))
                .collect(Collectors.toList());
    }

    public List<String> getSubjectsByBranch(String branch) {
        return questions.stream()
                .filter(q -> branch == null || q.getBranch().equals(branch))
                .map(Question::getSubject)
                .distinct()
                .collect(Collectors.toList());
    }

    public void saveQuestion(Question question) {
        questions.add(question);
    }

    public void updateQuestion(String id, Question updatedQuestion) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(id)) {
                questions.set(i, updatedQuestion);
                return;
            }
        }
    }

    public void deleteQuestion(String id) {
        questions.removeIf(q -> q.getId().equals(id));
    }

    public Question getQuestionById(String id) {
        return questions.stream().filter(q -> q.getId().equals(id)).findFirst().orElse(null);
    }

    // Result operations
    public void saveResult(QuizResult result) {
        results.add(result);
    }

    public List<QuizResult> getResultsByUsername(String username) {
        return results.stream()
                .filter(r -> r.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    // Schedule operations
    public void saveSchedule(QuizSchedule schedule) {
        String key = schedule.getBranch() + "_" + schedule.getSubject();
        schedules.put(key, schedule);
    }

    public QuizSchedule getSchedule(String branch, String subject) {
        String key = branch + "_" + subject;
        return schedules.get(key);
    }
}
