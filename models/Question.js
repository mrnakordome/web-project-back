const mongoose = require('mongoose');

const questionSchema = new mongoose.Schema({
  test: { type: String, required: true }, // Question text
  options: {
    A: { type: String, required: true },
    B: { type: String, required: true },
    C: { type: String, required: true },
    D: { type: String, required: true },
  },
  correctAnswer: { type: String, required: true }, // Correct option (e.g., 'A')
  categoryId: { type: Number, required: true },
  difficulty: { type: Number, default: 1.0 },
});

module.exports = mongoose.model('Question', questionSchema);
