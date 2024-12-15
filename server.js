const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

// Import mock data
const { adminMocks, userMocks, categoriesMock, questionsMock } = require('./mockData');

const app = express();
app.use(bodyParser.json());
app.use(cors());

/* ------------------- POST: Login ------------------- */
app.post('/login', (req, res) => {
  const { username, password, role } = req.body;
  console.log('Login Request:', { username, password, role });

  if (role === 'admin') {
    const admin = adminMocks.find(
      (a) => a.username === username && a.password === password && a.role === role
    );
    if (admin) {
      return res.json({
        id: admin.id,
        username: admin.username,
        role: admin.role,
        adminLevel: admin.adminLevel,
      });
    }
  } else if (role === 'user') {
    const user = userMocks.find(
      (u) => u.username === username && u.password === password && u.role === role
    );
    if (user) {
      return res.json({
        id: user.id,
        username: user.username,
        role: user.role,
      });
    }
  }
  res.status(401).json({ error: 'Invalid username or password' });
});

/* ------------------- GET: Categories ------------------- */
app.get('/categories', (req, res) => {
  res.json(categoriesMock);
});

/* ------------------- GET: Admin Details ------------------- */
app.get('/admin/:id', (req, res) => {
  const adminId = parseInt(req.params.id, 10);
  const admin = adminMocks.find((a) => a.id === adminId);

  if (admin) {
    const adminQuestions = admin.questions.map((qId) =>
      questionsMock.find((question) => question.id === qId)
    );
    res.json({
      id: admin.id,
      username: admin.username,
      followers: admin.followers,
      followin: admin.followin,
      questions: adminQuestions,
    });
  } else {
    res.status(404).json({ error: 'Admin not found' });
  }
});

/* ------------------- GET: User Details ------------------- */
app.get('/user/:id', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const user = userMocks.find((u) => u.id === userId);

  if (user) {
    res.json({
      id: user.id,
      username: user.username,
      followers: user.followers,
      following: user.points,
    });
  } else {
    res.status(404).json({ error: 'User not found' });
  }
});

/* ------------------- GET: Random Unanswered Question ------------------- */
app.get('/user/:id/questions/random', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const user = userMocks.find((u) => u.id === userId);

  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  // Get IDs of questions the user has already answered
  const answeredQuestionIds = user.questions.map((q) => q.questionId);

  // Filter questions not yet answered
  const unansweredQuestions = questionsMock.filter(
    (question) => !answeredQuestionIds.includes(question.id)
  );

  if (unansweredQuestions.length === 0) {
    return res.status(404).json({ error: 'No unanswered questions available.' });
  }

  // Select a random question
  const randomQuestion =
    unansweredQuestions[Math.floor(Math.random() * unansweredQuestions.length)];

  res.json(randomQuestion);
});

/* ------------------- POST: Submit Answer ------------------- */
app.post('/user/:id/questions/answer', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const { questionId, userAnswer } = req.body;

  const user = userMocks.find((u) => u.id === userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }

  // Check if the question exists
  const question = questionsMock.find((q) => q.id === questionId);
  if (!question) {
    return res.status(404).json({ error: 'Question not found' });
  }

  // Add the answered question to the user's history
  user.questions.push({ questionId, userAnswer });

  res.status(200).json({ message: 'Answer submitted successfully!' });
});

/* ------------------- GET: User Questions History ------------------- */
app.get('/user/:id/questions/history', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const user = userMocks.find((u) => u.id === userId);

  if (user) {
    const questionHistory = user.questions.map((userQ) => {
      const question = questionsMock.find((q) => q.id === userQ.questionId);
      return {
        questionText: question?.test || 'Unknown Question',
        userAnswer: userQ.userAnswer || 'N/A',
        correctAnswer: question?.correctAnswer || 'N/A',
      };
    });

    return res.json(questionHistory);
  } else {
    return res.status(404).json({ error: 'User not found' });
  }
});

/* ------------------- GET: Leaderboard ------------------- */
app.get('/leaderboard', (req, res) => {
  const sortedUsers = [...userMocks].sort((a, b) => b.points - a.points);
  res.json(sortedUsers);
});

/* ------------------- POST: Add New Question ------------------- */
app.post('/questions', (req, res) => {
  const { test, options, correctAnswer, categoryId, difficulty } = req.body;

  const category = categoriesMock.find((c) => c.id === parseInt(categoryId, 10));
  if (!category) {
    return res.status(400).json({ error: 'Invalid category ID' });
  }

  const newQuestion = {
    id: questionsMock.length + 1,
    test,
    options,
    correctAnswer,
    categoryId: category.id,
    difficulty,
  };

  questionsMock.push(newQuestion);
  res.status(201).json({ message: 'Question added successfully!', question: newQuestion });
});

/* ------------------- POST: Register ------------------- */
app.post('/register', (req, res) => {
  const { username, password, role } = req.body;

  if (!username || !password || !role) {
    return res.status(400).json({ error: 'All fields are required.' });
  }

  // Check for duplicates
  if (role === 'user' && userMocks.some((u) => u.username === username)) {
    return res.status(400).json({ error: 'Username already exists.' });
  }
  if (role === 'admin' && adminMocks.some((a) => a.username === username)) {
    return res.status(400).json({ error: 'Username already exists.' });
  }

  const newUser = {
    id: role === 'user' ? userMocks.length + 1 : adminMocks.length + 1,
    username,
    password,
    role,
    followers: 0,
    followin: 0,
    points: 0,
    questions: [],
  };

  if (role === 'user') {
    userMocks.push(newUser);
  } else if (role === 'admin') {
    adminMocks.push(newUser);
  }

  res.status(201).json({ message: 'Registration successful!' });
});


/* ------------------- SERVER START ------------------- */
const PORT = 5000;
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
