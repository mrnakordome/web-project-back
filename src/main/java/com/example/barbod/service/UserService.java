// src/main/java/com/example/barbod/service/UserService.java
package com.example.barbod.service;

import com.example.barbod.model.User;
import com.example.barbod.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) throws Exception {
        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new Exception("Username already exists.");
        }

        // Hash the password before saving
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public User loginUser(String username, String role) throws Exception {
        // Find user by username and role
        Optional<User> userOpt = userRepository.findByUsernameAndRole(username, role);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new Exception("Invalid username or password.");
    }

    public void Follow(String followerId, String followingId, String followingRole){
        Optional<User> follower = userRepository.findById(followerId);
        Optional<User> following = userRepository.findById(followingRole);
    }

    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findAdminById(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && "admin".equals(userOpt.get().getRole())) {
            return userOpt;
        }
        return Optional.empty();
    }
}
