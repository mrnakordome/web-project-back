const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const bcrypt = require('bcrypt');
const User = require('./models/User'); // User model
const Question = require('./models/Question'); // Question model
const Category = require('./models/Category');

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Connect to MongoDB
mongoose.connect('mongodb://127.0.0.1:27017/barbod', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
})
  .then(() => console.log('Connected to MongoDB'))
  .catch((err) => console.error('MongoDB connection error:', err));

// ------------------- POST: Register -------------------
app.post('/register', async (req, res) => {
  const { username, password, role } = req.body;

  if (!username || !password || !role) {
    return res.status(400).json({ error: 'All fields are required.' });
  }

  try {
    const existingUser = await User.findOne({ username });
    if (existingUser) {
      return res.status(400).json({ error: 'Username already exists.' });
    }

    // Hash the password before saving it
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      username,
      password: hashedPassword,
      role,
    });

    await newUser.save();

    res.status(201).json({ message: 'Registration successful!' });
  } catch (err) {
    console.error('Register Error:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- POST: Login -------------------
app.post('/login', async (req, res) => {
  const { username, password, role } = req.body;

  try {
    const user = await User.findOne({ username, role });
    if (user && await bcrypt.compare(password, user.password)) {
      return res.json({
        id: user._id,
        username: user.username,
        role: user.role,
      });
    }
    return res.status(401).json({ error: 'Invalid username or password' });
  } catch (err) {
    console.error('Login Error:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- GET: User Details -------------------
app.get('/user/:id', async (req, res) => {
  const { id } = req.params;

  try {
    // Find user by ID
    const user = await User.findById(id);

    if (user) {
      res.json({
        id: user._id,
        username: user.username,
        followersCount: user.followers?.length || 0,
        followingCount: user.followin,
      });
    } else {
      res.status(404).json({ error: 'User not found' });
    }
  } catch (err) {
    console.error('Error fetching user data:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- GET: Admin Details -------------------
app.get('/admin/:id', async (req, res) => {
  const { id } = req.params;

  try {
    // Check if ID is valid
    if (!mongoose.Types.ObjectId.isValid(id)) {
      return res.status(400).json({ error: 'Invalid admin ID format' });
    }

    // Find user by ID and ensure role is 'admin'
    const admin = await User.findOne({ _id: id, role: 'admin' }).populate('questions');

    if (admin) {
      res.json({
        id: admin._id,
        username: admin.username,
        adminLevel: admin.adminLevel,
        followin: admin.followin,
        followersCount: admin.followers?.length || 0,
        questions: admin.questions.map((q) => ({
          id: q._id,
          test: q.test,
          options: q.options,
          correctAnswer: q.correctAnswer,
          categoryId: q.categoryId,
          difficulty: q.difficulty,
        })),
      });
    } else {
      res.status(404).json({ error: 'Admin not found' });
    }
  } catch (error) {
    console.error('Error fetching admin details:', error.message);
    res.status(500).json({ error: 'Internal server error' });
  }
});
// ------------------- GET: Leaderboard -------------------
app.get('/leaderboard', async (req, res) => {
  try {
    const leaderboard = await User.find()
      .sort({ points: -1 })
      .select('username points');

    res.json({ leaderboard });
  } catch (err) {
    console.error('Error fetching leaderboard:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- GET: Categories -------------------
app.get('/categories', async (req, res) => {
  try {
    const categories = await Category.find();
    res.json(categories);
  } catch (err) {
    console.error('Error fetching categories:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- POST: Add New Category -------------------
app.post('/categories', async (req, res) => {
  const { name } = req.body;

  if (!name || typeof name !== 'string' || name.trim() === '') {
    return res.status(400).json({ error: 'Invalid category name' });
  }

  try {
    const newCategory = new Category({ name: name.trim() });
    await newCategory.save();

    res.status(201).json({ message: 'Category added successfully!', category: newCategory });
  } catch (err) {
    console.error('Add Category Error:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- GET: Admin Questions -------------------
app.get('/admin/:id/questions', async (req, res) => {
  const adminId = req.params.id;

  try {
    const admin = await User.findById(adminId).populate('questions');
    if (!admin || admin.role !== 'admin') {
      return res.status(404).json({ error: 'Admin not found.' });
    }

    res.json({ questions: admin.questions });
    console.log(admin.questions);
  } catch (err) {
    console.error('Error fetching admin questions:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- GET: Random Unanswered Question -------------------
app.get('/user/:id/questions/random', async (req, res) => {
  const userId = req.params.id;

  try {
    const user = await User.findById(userId).populate('questions');
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const answeredQuestionIds = user.questions.map((q) => q._id.toString());
    const unansweredQuestions = await Question.find({
      _id: { $nin: answeredQuestionIds },
    });

    if (unansweredQuestions.length === 0) {
      return res.status(404).json({ error: 'No unanswered questions available.' });
    }

    const randomQuestion =
      unansweredQuestions[Math.floor(Math.random() * unansweredQuestions.length)];

    res.json(randomQuestion);
  } catch (err) {
    console.error('Error fetching random question:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- POST: Submit Answer -------------------
app.post('/user/:id/questions/answer', async (req, res) => {
  const userId = req.params.id;
  const { questionId, userAnswer } = req.body;

  console.log("Answer");
  console.log(questionId);
  console.log(userAnswer);

  try {
    const user = await User.findById(userId);
    const question = await Question.findById(questionId);

    if (!user || !question) {
      console.log("User of Question not found");
      return res.status(404).json({ error: 'User or question not found.' });
    }

    if (question.correctAnswer === userAnswer) {
      user.points += question.difficulty;
      await user.save();
      return res.json({ message: 'Correct answer!', points: user.points });
    } else {
      return res.json({ message: 'Wrong answer.' });
    }
  } catch (err) {
    console.log(err);
    console.error('Submit Answer Error:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// ------------------- POST: Add New Question -------------------
app.post('/questions', async (req, res) => {
  const { adminId, test, options, correctAnswer, categoryId, difficulty } = req.body;

  if (!adminId || !test || !options || !correctAnswer || !categoryId || !difficulty) {
    return res.status(400).json({ error: 'All fields are required.' });
  }

  try {
    const admin = await User.findById(adminId);
    if (!admin || admin.role !== 'admin') {
      return res.status(404).json({ error: 'Admin not found or invalid role.' });
    }

    const category = await Category.findOne({ name: categoryId });
    if (!category) {
      return res.status(404).json({ error: 'Category not found.' });
    }

    const newQuestion = new Question({
      test,
      options,
      correctAnswer,
      categoryId: categoryId,
      difficulty,
    });

    await newQuestion.save();

    admin.questions.push(newQuestion._id);
    await admin.save();

    res.status(201).json({
      message: 'Question added successfully!',
      question: newQuestion,
    });
  } catch (err) {
    console.log("Add Qustion Error:", err);
    console.error('Add Question Error:', err);
    res.status(500).json({ error: 'Server error' });
  }
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
