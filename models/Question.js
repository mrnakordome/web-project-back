const mongoose = require('mongoose');

const questionSchema = new mongoose.Schema({
  test: { type: String, required: true },
  options: {
    A: { type: String, required: true },
    B: { type: String, required: true },
    C: { type: String, required: true },
    D: { type: String, required: true },
  },
  correctAnswer: { type: String, required: true },
  categoryId: { type: String, required: true }, // Reference to Category
  difficulty: { type: Number, required: true },
});

module.exports = mongoose.model('Question', questionSchema);
