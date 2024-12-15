const adminMocks = [
  {
    id: 1,
    username: 'admin1',
    password: '1',
    role: 'admin',
    adminLevel: 1,
    followin: 10,
    followers: 100,
    questions: ['What is leadership?', 'Define project management.'],
  },
  {
    id: 2,
    username: 'admin2',
    password: '1',
    role: 'admin',
    adminLevel: 2,
    followin: 20,
    followers: 150,
    questions: ['What is teamwork?', 'What is agile methodology?'],
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

const categoriesMock = [
  { id: 1, name: 'Math' },
  { id: 2, name: 'Biology' },
  { id: 3, name: 'Physics' },
  { id: 4, name: 'Chemistry' },
  { id: 5, name: 'Computer Science' },
  { id: 6, name: 'Economics' },
];

module.exports = { adminMocks, userMocks, categoriesMock };
