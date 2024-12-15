const adminMocks = [
    { id: 1, username: 'admin1', password: '1', role: 'admin', adminLevel: 1 },
    { id: 2, username: 'admin2', password: '1', role: 'admin', adminLevel: 2 },
  ];
  
  const userMocks = [
    {
      id: 1,
      username: 'user1',
      password: '1',
      role: 'user',
      points: 50,
      followers: 120, // New field
      score: 1000, // New field
      question: ['What is React?', 'What is Node.js?'], // New field
    },
    {
      id: 2,
      username: 'user2',
      password: '1',
      role: 'user',
      points: 30,
      followers: 80, // New field
      score: 800, // New field
      question: ['Explain Redux', 'What is Express.js?'], // New field
    },
    {
      id: 3,
      username: 'user3',
      password: '1',
      role: 'user',
      points: 100,
      followers: 200, // New field
      score: 1200, // New field
      question: ['What is MongoDB?', 'What is a REST API?'], // New field
    },
  ];
  
  module.exports = { adminMocks, userMocks };
  