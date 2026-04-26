// Global State
const API_BASE = '/api';
let currentQuestions = [];
let quizTimer = null;
let timeLeft = 600; // 10 minutes default
let currentSubject = null;

// --- Auth Functions ---
async function handleLogin(e) {
    if(e) e.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');

    try {
        const res = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();

        if (res.ok) {
            localStorage.setItem('user', JSON.stringify(data));
            if (data.role === 'ADMIN') {
                window.location.href = 'admin.html';
            } else {
                window.location.href = 'student.html';
            }
        } else {
            errorEl.textContent = data.message || 'Login failed';
        }
    } catch (err) {
        errorEl.textContent = 'Server error. Please try again later.';
    }
}

async function handleRegister(e) {
    if(e) e.preventDefault();
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;
    const branch = document.getElementById('regBranch') ? document.getElementById('regBranch').value : 'N/A';
    const subject = document.getElementById('regSubject') ? document.getElementById('regSubject').value || 'N/A' : 'N/A';
    const adminPassword = document.getElementById('regAdminPassword') ? document.getElementById('regAdminPassword').value : '';
    const errorEl = document.getElementById('regError');
    const successEl = document.getElementById('regSuccess');

    errorEl.textContent = '';
    successEl.textContent = '';

    try {
        const res = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, role, branch, subject, adminPassword })
        });
        const data = await res.json();

        if (res.ok) {
            successEl.textContent = 'Registration successful! You can now login.';
            document.getElementById('registerForm').reset();
            setTimeout(() => switchTab('login'), 2000);
        } else {
            errorEl.textContent = data.message || 'Registration failed';
        }
    } catch (err) {
        errorEl.textContent = 'Server error. Please try again later.';
    }
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

function isLoggedIn() {
    return localStorage.getItem('user') !== null;
}

function getUserRole() {
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? user.role : null;
}

function getUsername() {
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? user.username : null;
}

function getUserBranch() {
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? user.branch : null;
}

function getUserSubject() {
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? user.subject : null;
}

// --- Student Functions ---
async function loadSubjects() {
    if (!document.getElementById('subjectList')) return; // Not on student page
    
    try {
        const branch = getUserBranch();
        const res = await fetch(`${API_BASE}/questions/subjects?branch=${encodeURIComponent(branch)}`);
        const subjects = await res.json();
        
        const subjectList = document.getElementById('subjectList');
        subjectList.innerHTML = '';
        
        if (subjects.length === 0) {
            subjectList.innerHTML = '<p>No subjects available for your branch yet.</p>';
            return;
        }
        
        subjects.forEach(subject => {
            const btn = document.createElement('button');
            btn.className = 'btn btn-primary btn-lg';
            btn.style.width = '100%';
            btn.textContent = subject + ' Quiz';
            btn.onclick = () => startQuiz(subject);
            subjectList.appendChild(btn);
        });
    } catch (err) {
        console.error(err);
        document.getElementById('subjectList').innerHTML = '<p>Error loading subjects.</p>';
    }
}

async function startQuiz(subject) {
    currentSubject = subject;
    
    // Check schedule first
    try {
        const branch = getUserBranch();
        const schedRes = await fetch(`${API_BASE}/schedule?branch=${encodeURIComponent(branch)}&subject=${encodeURIComponent(subject)}`);
        if (schedRes.ok) {
            const schedule = await schedRes.json();
            const now = new Date();
            const startTime = new Date(schedule.startTime);
            const endTime = new Date(schedule.endTime);
            
            if (now < startTime) {
                alert(`This quiz has not started yet. It starts at ${startTime.toLocaleString()}`);
                return;
            }
            if (now > endTime) {
                alert(`This quiz has ended. It ended at ${endTime.toLocaleString()}`);
                return;
            }
        }
    } catch (err) {
        console.warn("Could not fetch schedule, proceeding anyway");
    }

    document.getElementById('studentDashboard').style.display = 'none';
    document.getElementById('quizView').style.display = 'block';
    
    try {
        const branch = getUserBranch();
        const res = await fetch(`${API_BASE}/questions?branch=${encodeURIComponent(branch)}&subject=${encodeURIComponent(subject)}`);
        currentQuestions = await res.json();
        renderQuiz();
        startTimer(600); // 10 minutes
    } catch (err) {
        alert('Failed to load questions.');
        backToDashboard();
    }
}

function renderQuiz() {
    const container = document.getElementById('questionContainer');
    container.innerHTML = '';

    if (currentQuestions.length === 0) {
        container.innerHTML = '<p>No questions available for this quiz.</p>';
        return;
    }

    currentQuestions.forEach((q, index) => {
        const div = document.createElement('div');
        div.className = 'question-item';
        div.innerHTML = `
            <div class="question-text">${index + 1}. ${q.text}</div>
            <div class="options-grid">
                <label class="option-label">
                    <input type="radio" name="q_${q.id}" value="A"> A) ${q.optionA}
                </label>
                <label class="option-label">
                    <input type="radio" name="q_${q.id}" value="B"> B) ${q.optionB}
                </label>
                <label class="option-label">
                    <input type="radio" name="q_${q.id}" value="C"> C) ${q.optionC}
                </label>
                <label class="option-label">
                    <input type="radio" name="q_${q.id}" value="D"> D) ${q.optionD}
                </label>
            </div>
        `;
        container.appendChild(div);
    });
}

function startTimer(seconds) {
    timeLeft = seconds;
    const timerDisplay = document.getElementById('timer');
    const progressBar = document.getElementById('quizProgress');
    const totalTime = seconds;

    clearInterval(quizTimer);
    quizTimer = setInterval(() => {
        timeLeft--;
        
        const minutes = Math.floor(timeLeft / 60);
        const secs = timeLeft % 60;
        timerDisplay.textContent = `Time Left: ${minutes}:${secs < 10 ? '0' : ''}${secs}`;
        
        // Update progress bar
        const progress = ((totalTime - timeLeft) / totalTime) * 100;
        progressBar.style.width = `${progress}%`;

        if (timeLeft <= 0) {
            clearInterval(quizTimer);
            submitQuiz();
        }
    }, 1000);
}

async function submitQuiz() {
    clearInterval(quizTimer);
    
    const answers = {};
    currentQuestions.forEach(q => {
        const selected = document.querySelector(`input[name="q_${q.id}"]:checked`);
        if (selected) {
            answers[q.id] = selected.value;
        }
    });

    try {
        const res = await fetch(`${API_BASE}/quiz/submit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: getUsername(),
                branch: getUserBranch(),
                subject: currentSubject,
                answers: answers
            })
        });
        const result = await res.json();
        
        showResultView(result);
    } catch (err) {
        alert('Failed to submit quiz.');
    }
}

function showResultView(result) {
    document.getElementById('quizView').style.display = 'none';
    document.getElementById('resultView').style.display = 'block';
    
    document.getElementById('finalScore').textContent = result.score;
    document.getElementById('finalTotal').textContent = result.totalQuestions;
    
    const percentage = result.totalQuestions > 0 
        ? Math.round((result.score / result.totalQuestions) * 100) 
        : 0;
    document.getElementById('scorePercentage').textContent = `${percentage}%`;
}

async function viewMyResults() {
    const section = document.getElementById('myResultsSection');
    const tbody = document.getElementById('resultsTableBody');
    
    if (section.style.display === 'block') {
        section.style.display = 'none';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/quiz/results/${getUsername()}`);
        const results = await res.json();
        
        tbody.innerHTML = '';
        if (results.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align: center">No past results found.</td></tr>';
        } else {
            results.forEach((r, idx) => {
                const percentage = r.totalQuestions > 0 ? Math.round((r.score / r.totalQuestions) * 100) : 0;
                tbody.innerHTML += `
                    <tr>
                        <td>${idx + 1}</td>
                        <td>${r.subject || 'General'}</td>
                        <td>${r.score}</td>
                        <td>${r.totalQuestions}</td>
                        <td>${percentage}%</td>
                    </tr>
                `;
            });
        }
        section.style.display = 'block';
    } catch (err) {
        alert('Failed to load results.');
    }
}

function backToDashboard() {
    document.getElementById('resultView').style.display = 'none';
    document.getElementById('studentDashboard').style.display = 'block';
    document.getElementById('myResultsSection').style.display = 'none';
}

// --- Admin Functions ---
async function loadAdminQuestions() {
    if (!document.getElementById('questionsTableBody')) return; // Not on admin page
    
    try {
        const branch = getUserBranch();
        const subject = getUserSubject();
        const res = await fetch(`${API_BASE}/questions?branch=${encodeURIComponent(branch)}&subject=${encodeURIComponent(subject)}`);
        const questions = await res.json();
        
        const tbody = document.getElementById('questionsTableBody');
        tbody.innerHTML = '';
        
        questions.forEach(q => {
            tbody.innerHTML += `
                <tr>
                    <td>${q.text.substring(0, 50)}${q.text.length > 50 ? '...' : ''}</td>
                    <td>${q.optionA}</td>
                    <td>${q.optionB}</td>
                    <td>${q.optionC}</td>
                    <td>${q.optionD}</td>
                    <td><strong>${q.correctOption}</strong></td>
                    <td>
                        <button onclick='editQuestion(${JSON.stringify(q).replace(/'/g, "&#39;")})' class="btn btn-sm btn-outline">Edit</button>
                        <button onclick="deleteQuestion('${q.id}')" class="btn btn-sm btn-danger" style="margin-left: 0.5rem">Delete</button>
                    </td>
                </tr>
            `;
        });
    } catch (err) {
        alert('Failed to load questions.');
    }
}

function openQuestionModal() {
    document.getElementById('questionModal').style.display = 'block';
    document.getElementById('questionForm').reset();
    document.getElementById('qId').value = '';
    document.getElementById('modalTitle').textContent = 'Add Question';
}

function closeQuestionModal() {
    document.getElementById('questionModal').style.display = 'none';
}

function editQuestion(q) {
    document.getElementById('qId').value = q.id;
    document.getElementById('qText').value = q.text;
    document.getElementById('qOptA').value = q.optionA;
    document.getElementById('qOptB').value = q.optionB;
    document.getElementById('qOptC').value = q.optionC;
    document.getElementById('qOptD').value = q.optionD;
    document.getElementById('qCorrect').value = q.correctOption;
    
    document.getElementById('modalTitle').textContent = 'Edit Question';
    document.getElementById('questionModal').style.display = 'block';
}

async function handleQuestionSubmit(e) {
    if(e) e.preventDefault();
    
    const id = document.getElementById('qId').value;
    const question = {
        text: document.getElementById('qText').value,
        optionA: document.getElementById('qOptA').value,
        optionB: document.getElementById('qOptB').value,
        optionC: document.getElementById('qOptC').value,
        optionD: document.getElementById('qOptD').value,
        correctOption: document.getElementById('qCorrect').value,
        branch: getUserBranch(),
        subject: getUserSubject()
    };

    try {
        let res;
        if (id) {
            // Update
            res = await fetch(`${API_BASE}/questions/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(question)
            });
        } else {
            // Create
            res = await fetch(`${API_BASE}/questions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(question)
            });
        }

        if (res.ok) {
            closeQuestionModal();
            loadAdminQuestions();
        } else {
            alert('Failed to save question.');
        }
    } catch (err) {
        alert('Server error.');
    }
}

async function deleteQuestion(id) {
    if (!confirm('Are you sure you want to delete this question?')) return;
    
    try {
        const res = await fetch(`${API_BASE}/questions/${id}`, {
            method: 'DELETE'
        });
        if (res.ok) {
            loadAdminQuestions();
        } else {
            alert('Failed to delete question.');
        }
    } catch (err) {
        alert('Server error.');
    }
}

// --- Schedule Functions ---
async function loadSchedule() {
    if (!document.getElementById('scheduleForm')) return;
    try {
        const branch = getUserBranch();
        const subject = getUserSubject();
        const res = await fetch(`${API_BASE}/schedule?branch=${encodeURIComponent(branch)}&subject=${encodeURIComponent(subject)}`);
        if (res.ok) {
            const schedule = await res.json();
            if (schedule.startTime) {
                // Need to strip trailing Z or milliseconds if they exist to fit datetime-local
                document.getElementById('schedStart').value = schedule.startTime.substring(0, 16);
            }
            if (schedule.endTime) {
                document.getElementById('schedEnd').value = schedule.endTime.substring(0, 16);
            }
        }
    } catch (err) {
        console.error(err);
    }
}

async function handleScheduleSubmit(e) {
    if(e) e.preventDefault();
    const branch = getUserBranch();
    const subject = getUserSubject();
    const startTime = document.getElementById('schedStart').value;
    const endTime = document.getElementById('schedEnd').value;
    
    try {
        const res = await fetch(`${API_BASE}/schedule`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ branch, subject, startTime, endTime })
        });
        if (res.ok) {
            document.getElementById('scheduleMessage').textContent = 'Schedule saved successfully!';
            setTimeout(() => document.getElementById('scheduleMessage').textContent = '', 3000);
        } else {
            document.getElementById('scheduleMessage').textContent = 'Failed to save schedule.';
            document.getElementById('scheduleMessage').style.color = 'red';
        }
    } catch (err) {
        document.getElementById('scheduleMessage').textContent = 'Error saving schedule.';
        document.getElementById('scheduleMessage').style.color = 'red';
    }
}

// Event Listeners setup on load
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);
    
    const registerForm = document.getElementById('registerForm');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);
    
    const questionForm = document.getElementById('questionForm');
    if (questionForm) questionForm.addEventListener('submit', handleQuestionSubmit);
    
    const scheduleForm = document.getElementById('scheduleForm');
    if (scheduleForm) scheduleForm.addEventListener('submit', handleScheduleSubmit);
});
