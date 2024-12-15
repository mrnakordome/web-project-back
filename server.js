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

  if (role === 'admin') {
    const admin = adminMocks.find(
      (a) => a.username === username && a.password === password
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
      (u) => u.username === username && u.password === password
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

/* ------------------- GET: Leaderboard ------------------- */
app.get('/leaderboard', (req, res) => {
  const leaderboard = userMocks
    .map((user) => ({ id: user.id, username: user.username, score: user.points }))
    .sort((a, b) => b.score - a.score);

  res.json({ leaderboard });
});



/* ------------------- POST: Register ------------------- */
app.post('/register', (req, res) => {
  const { username, password, role } = req.body;

  if (!username || !password || !role) {
    return res.status(400).json({ error: 'All fields are required.' });
  }

  // Check if the username already exists
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
    followers: [], // Initialize as an empty array
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

/* ------------------- POST: Follow User/Admin ------------------- */
app.post('/follow', (req, res) => {
  const { followerId, followingId, role } = req.body;

  const follower = userMocks.find((u) => u.id === followerId);
  const following = role === 'user'
    ? userMocks.find((u) => u.id === followingId)
    : adminMocks.find((a) => a.id === followingId);

  if (!follower || !following) {
    return res.status(404).json({ error: 'User or Admin not found.' });
  }

  if (!following.followers.includes(followerId)) {
    following.followers.push(followerId);
    follower.points += 1; // Increase points for the follower
    return res.status(200).json({ message: 'Followed successfully!' });
  } else {
    return res.status(400).json({ error: 'Already following this user/admin.' });
  }
});



/* ------------------- GET: Categories ------------------- */
app.get('/categories', (req, res) => {
  res.json(categoriesMock);
});

/* ------------------- POST: Add New Category ------------------- */
app.post('/categories', (req, res) => {
  const { name } = req.body;

  if (!name || typeof name !== 'string' || name.trim() === '') {
    return res.status(400).json({ error: 'Invalid category name' });
  }

  const newCategory = {
    id: categoriesMock.length + 1,
    name: name.trim(),
  };

  categoriesMock.push(newCategory);
  res.status(201).json({ message: 'Category added successfully!', category: newCategory });
});

/* ------------------- GET: Admin Questions ------------------- */
app.get('/admin/:id/questions', (req, res) => {
  const adminId = parseInt(req.params.id, 10);

  // Find the admin
  const admin = adminMocks.find((a) => a.id === adminId);
  if (!admin) {
    return res.status(404).json({ error: 'Admin not found.' });
  }

  // Fetch questions linked to the admin
  const adminQuestions = admin.questions
    .map((qId) => questionsMock.find((question) => question.id === qId))
    .filter((question) => question !== undefined); // Filter out invalid IDs

  // Return the questions
  res.json({ questions: adminQuestions });
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
      followers: admin.followers.length, // Return the count of followers
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
      followers: user.followers.length, // Return the count of followers
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

  const answeredQuestionIds = user.questions.map((q) => q.questionId);
  const unansweredQuestions = questionsMock.filter(
    (question) => !answeredQuestionIds.includes(question.id)
  );

  if (unansweredQuestions.length === 0) {
    return res.status(404).json({ error: 'No unanswered questions available.' });
  }

  const randomQuestion =
    unansweredQuestions[Math.floor(Math.random() * unansweredQuestions.length)];

  res.json(randomQuestion);
});

/* ------------------- POST: Submit Answer ------------------- */
app.post('/user/:id/questions/answer', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const { questionId, userAnswer } = req.body;

  const user = userMocks.find((u) => u.id === userId);
  const question = questionsMock.find((q) => q.id === questionId);

  if (!user) return res.status(404).json({ error: 'User not found.' });
  if (!question) return res.status(404).json({ error: 'Question not found.' });

  if (question.correctAnswer === userAnswer) {
    user.points += question.difficulty;
    return res.json({ message: 'Correct answer!', points: user.points });
  } else {
    return res.json({ message: 'Wrong answer.' });
  }
});


/* ------------------- GET: Search User by Username ------------------- */
app.get('/search/user', (req, res) => {
  const { username } = req.query; // Query parameter
  const user = userMocks.find((u) => u.username === username);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }
  res.status(200).json(user);
});

/* ------------------- GET: Search Admin by Username ------------------- */
app.get('/search/admin', (req, res) => {
  const { username } = req.query; // Query parameter
  const admin = adminMocks.find((a) => a.username === username);
  if (!admin) {
    return res.status(404).json({ error: 'Admin not found' });
  }
  res.status(200).json(admin);
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

/* ------------------- POST: Add New Question ------------------- */
app.post('/questions', (req, res) => {
  const { adminId, test, options, correctAnswer, categoryId, difficulty } = req.body;

  // Validate input fields
  if (!adminId || !test || !options || !correctAnswer || !categoryId || !difficulty) {
    return res.status(400).json({ error: 'All fields are required.' });
  }

  // Find the category
  const category = categoriesMock.find((c) => c.id === parseInt(categoryId, 10));
  if (!category) {
    return res.status(400).json({ error: 'Invalid category ID.' });
  }

  // Generate a new question ID
  const newQuestionId = questionsMock.length > 0
    ? Math.max(...questionsMock.map((q) => q.id)) + 1
    : 1;

  // Create the new question object
  const newQuestion = {
    id: newQuestionId,
    test,
    options,
    correctAnswer,
    categoryId: category.id,
    difficulty: parseFloat(difficulty),
  };

  // Add the new question to questionsMock
  questionsMock.push(newQuestion);

  // Link the question ID to the admin's question array
  const admin = adminMocks.find((a) => a.id === parseInt(adminId, 10));
  if (!admin) {
    return res.status(404).json({ error: 'Admin not found.' });
  }

  admin.questions.push(newQuestionId);

  // Send response
  res.status(201).json({
    message: 'Question added successfully!',
    question: newQuestion,
  });
});

/* ------------------- SERVER START ------------------- */
const PORT = 5000;
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
