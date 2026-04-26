# Online Quiz System (Java Full Stack)

A beginner-friendly Online Quiz System built with Java, Spring Boot, HTML, CSS, and JavaScript.

## Features

- **User Registration & Login**: Role-based access for Students and Admins.
- **Admin Dashboard**: Admins can add, update, and delete questions.
- **Student Interface**: Students can start a quiz, see a 10-minute timer, and view progress.
- **Auto Score Calculation**: Quiz submits automatically when the timer ends or manually. Score and percentage are calculated.
- **In-Memory Storage**: Uses Java collections (HashMap, ArrayList) instead of a database for simplicity.
- **Modern UI**: Clean, responsive blue and white color scheme.

## Tech Stack

- **Backend**: Java 17, Spring Boot, REST APIs.
- **Frontend**: HTML5, Vanilla CSS3, Vanilla JavaScript.

## Setup & Running the Application

### Prerequisites
- Java Development Kit (JDK) 17 or higher installed.

### Steps to Run

1. Open your terminal or command prompt.
2. Navigate to the project root directory (`online-quiz`).
3. Run the application using the Gradle wrapper:
   - On Windows:
     ```bash
     .\gradlew.bat bootRun
     ```
   - On Mac/Linux:
     ```bash
     ./gradlew bootRun
     ```

### How to Use

1. Once the server is running, open a web browser and go to: `http://localhost:8080`
2. **For Admin Access**:
   - The system creates a default admin account on startup.
   - **Username**: `admin`
   - **Password**: `admin123`
   - You can log in and start adding quiz questions.
3. **For Student Access**:
   - Register a new account on the login page and select "Student" role.
   - Login with your new credentials to take the quiz.
