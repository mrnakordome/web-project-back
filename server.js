const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const bcrypt = require('bcrypt');
const User = require('./models/User'); // User model
const Question = require('./models/Question'); // Question model

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

// Start the server
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
