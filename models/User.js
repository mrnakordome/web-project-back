const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ['user', 'admin'], required: true },
  adminLevel: { type: Number, default: 0 },
  followin: { type: Number, default: 0 },
  followers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  questions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Question' }],
  points: { type: Number, default: 0 },
  answeredQuestions: [{ 
    questionId: { type: mongoose.Schema.Types.ObjectId, ref: 'Question' },
    answer: { type: String }
  }]
});

module.exports = mongoose.model('User', userSchema);
