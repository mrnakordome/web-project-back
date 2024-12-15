const adminMocks = [
  {
    id: 1,
    username: 'admin1',
    password: '1',
    role: 'admin',
    adminLevel: 1,
    followin: 10,
    followers: 100,
    questions: [1, 2],
  },
  {
    id: 2,
    username: 'admin2',
    password: '1',
    role: 'admin',
    adminLevel: 2,
    followin: 20,
    followers: 150,
    questions: [3, 4],
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
    questions: [
      { questionId: 5, userAnswer: 'A' },
      { questionId: 6, userAnswer: 'B' },
    ],
  },
  {
    id: 2,
    username: 'user2',
    password: '1',
    role: 'user',
    points: 30,
    followers: 80,
    score: 800,
    questions: [
      { questionId: 7, userAnswer: 'C' },
      { questionId: 8, userAnswer: 'B' },
    ],
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

const questionsMock = [
  { id: 5, test: 'What is gravity?', options: { A: 'Force of attraction', B: 'Magnetic force', C: 'Electric force', D: 'Friction' }, correctAnswer: 'A', categoryId: 3, difficulty: 3.5 },
  { id: 6, test: 'What is acceleration?', options: { A: 'Rate of speed', B: 'Rate of velocity change', C: 'Speed', D: 'Position' }, correctAnswer: 'B', categoryId: 3, difficulty: 4.0 },
  { id: 7, test: 'What is H2O?', options: { A: 'Oxygen', B: 'Nitrogen', C: 'Water', D: 'Helium' }, correctAnswer: 'C', categoryId: 4, difficulty: 2.0 },
  { id: 8, test: 'What is an acid?', options: { A: 'Neutral substance', B: 'Donor of protons', C: 'Accepts electrons', D: 'Reduces metals' }, correctAnswer: 'B', categoryId: 4, difficulty: 3.5 },
];

module.exports = { adminMocks, userMocks, categoriesMock, questionsMock };
