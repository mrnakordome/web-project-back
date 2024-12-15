const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');

// Import mock data
const { adminMocks, userMocks } = require('./mockData');

const app = express();
app.use(bodyParser.json());
app.use(cors());

// Login endpoint
app.post('/login', (req, res) => {
  const { username, password, role } = req.body;

  if (role === 'admin') {
    const admin = adminMocks.find(
      a => a.username === username && a.password === password && a.role === role
    );
    if (admin) {
      res.json({
        id: admin.id,
        username: admin.username,
        role: admin.role,
        adminLevel: admin.adminLevel,
      });
    } else {
      res.status(401).json({ error: 'Invalid admin username or password' });
    }
  } else if (role === 'user') {
    const user = userMocks.find(
      u => u.username === username && u.password === password && u.role === role
    );
    if (user) {
      res.json({
        id: user.id,
        username: user.username,
        role: user.role,
      });
    } else {
      res.status(401).json({ error: 'Invalid user username or password' });
    }
  } else {
    res.status(400).json({ error: 'Invalid role specified' });
  }
});

// Get user details endpoint
app.get('/user/:id', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const user = userMocks.find(u => u.id === userId);

  if (user) {
    res.json({
      id: user.id,
      username: user.username,
      followers: user.followers,
      following: user.points, // Using `points` as `following` for now
    });
  } else {
    res.status(404).json({ error: 'User not found' });
  }
});

const PORT = 5000;
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
