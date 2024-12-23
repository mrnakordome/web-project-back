const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ['user', 'admin'], required: true }, // Role determines if user is an admin
  adminLevel: { type: Number, default: 0 }, // Specific to admins
  followin: { type: Number, default: 0 },
  followers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], // References users (followers)
  questions: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Question' }], // Admins only
});

module.exports = mongoose.model('User', userSchema);
