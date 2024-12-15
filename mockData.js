const adminMocks = [
  {
    id: 1,
    username: 'admin1',
    password: '1',
    role: 'admin',
    adminLevel: 1,
    followin: 10, // New field
    followers: 100, // New field
    questions: ['What is leadership?', 'Define project management.'], // New field
  },
  {
    id: 2,
    username: 'admin2',
    password: '1',
    role: 'admin',
    adminLevel: 2,
    followin: 20, // New field
    followers: 150, // New field
    questions: ['What is teamwork?', 'What is agile methodology?'], // New field
  },
];

const userMocks = [
  {
    id: 1,
    username: 'user1',
    password: '1',
    role: 'user',
    points: 50,
    followers: 120,
    score: 1000,
    question: ['What is React?', 'What is Node.js?'],
  },
  {
    id: 2,
    username: 'user2',
    password: '1',
    role: 'user',
    points: 30,
    followers: 80,
    score: 800,
    question: ['Explain Redux', 'What is Express.js?'],
  },
  {
    id: 3,
    username: 'user3',
    password: '1',
    role: 'user',
    points: 100,
    followers: 200,
    score: 1200,
    question: ['What is MongoDB?', 'What is a REST API?'],
  },
];

module.exports = { adminMocks, userMocks };
