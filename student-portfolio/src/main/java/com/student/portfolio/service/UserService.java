package com.student.portfolio.service;

import com.student.portfolio.entity.User;
import com.student.portfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Ensure student/admin role is captured correctly, defaults can be set in controller
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User current = userOpt.get();
            // Keeping it simple with plain text passwords for this student project
            if (current.getPassword().equals(password)) {
                return current;
            }
        }
        return null; // Invalid credentials
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user); // update the password
            return true;
        }
        return false; // Email not found
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
