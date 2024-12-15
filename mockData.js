const adminMocks = [
  {
    id: 1,
    username: 'admin1',
    password: '1',
    role: 'admin',
    adminLevel: 1,
    followin: 10,
    followers: 100,
    questions: [1, 2], // References to question IDs
  },
  {
    id: 2,
    username: 'admin2',
    password: '1',
    role: 'admin',
    adminLevel: 2,
    followin: 20,
    followers: 150,
    questions: [3, 4], // References to question IDs
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
    question: [5, 6], // References to question IDs
  },
  {
    id: 2,
    username: 'user2',
    password: '1',
    role: 'user',
    points: 30,
    followers: 80,
    score: 800,
    question: [7, 8], // References to question IDs
  },
  {
    id: 3,
    username: 'user3',
    password: '1',
    role: 'user',
    points: 100,
    followers: 200,
    score: 1200,
    question: [9, 10], // References to question IDs
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
  {
    id: 1,
    test: 'What is 2 + 2?',
    options: { A: '3', B: '4', C: '5', D: '6' },
    correctAnswer: 'B',
    categoryId: 1, // Math
    difficulty: 2.5,
  },
  {
    id: 2,
    test: 'What is the square root of 16?',
    options: { A: '2', B: '3', C: '4', D: '5' },
    correctAnswer: 'C',
    categoryId: 1, // Math
    difficulty: 3.0,
  },
  {
    id: 3,
    test: 'What is a cell nucleus?',
    options: { A: 'Brain of the cell', B: 'Storage unit', C: 'Energy producer', D: 'Waste disposer' },
    correctAnswer: 'A',
    categoryId: 2, // Biology
    difficulty: 4.0,
  },
  {
    id: 4,
    test: 'What is DNA?',
    options: { A: 'A protein', B: 'Genetic material', C: 'A lipid', D: 'A carbohydrate' },
    correctAnswer: 'B',
    categoryId: 2, // Biology
    difficulty: 4.5,
  },
  {
    id: 5,
    test: 'What is gravity?',
    options: { A: 'Force of attraction', B: 'Magnetic force', C: 'Electric force', D: 'Friction' },
    correctAnswer: 'A',
    categoryId: 3, // Physics
    difficulty: 3.5,
  },
  {
    id: 6,
    test: 'What is acceleration?',
    options: { A: 'Rate of speed', B: 'Rate of velocity change', C: 'Speed', D: 'Position' },
    correctAnswer: 'B',
    categoryId: 3, // Physics
    difficulty: 4.0,
  },
  {
    id: 7,
    test: 'What is H2O?',
    options: { A: 'Oxygen', B: 'Nitrogen', C: 'Water', D: 'Helium' },
    correctAnswer: 'C',
    categoryId: 4, // Chemistry
    difficulty: 2.0,
  },
  {
    id: 8,
    test: 'What is an acid?',
    options: { A: 'Neutral substance', B: 'Donor of protons', C: 'Accepts electrons', D: 'Reduces metals' },
    correctAnswer: 'B',
    categoryId: 4, // Chemistry
    difficulty: 3.5,
  },
  {
    id: 9,
    test: 'What is React?',
    options: { A: 'Library for UI', B: 'Database', C: 'Programming language', D: 'IDE' },
    correctAnswer: 'A',
    categoryId: 5, // Computer Science
    difficulty: 4.0,
  },
  {
    id: 10,
    test: 'What is an API?',
    options: { A: 'User interface', B: 'Server endpoint', C: 'Application Programming Interface', D: 'Browser' },
    correctAnswer: 'C',
    categoryId: 5, // Computer Science
    difficulty: 4.2,
  },
];

module.exports = { adminMocks, userMocks, categoriesMock, questionsMock };
