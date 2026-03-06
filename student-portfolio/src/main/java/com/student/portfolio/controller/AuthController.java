package com.student.portfolio.controller;

import com.student.portfolio.entity.User;
import com.student.portfolio.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Force new users to be students
        if(user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_STUDENT");
        }
        
        try {
            User savedUser = userService.registerUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username or email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String captchaResult = loginRequest.get("captchaResult");
        String captchaExpected = loginRequest.get("captchaExpected");

        // Captcha validation
        if(captchaResult == null || captchaExpected == null || !captchaResult.equals(captchaExpected)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid captcha");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        User user = userService.loginUser(username, password);
        if (user != null) {
            session.setAttribute("loggedInUser", user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user); // You probably wouldn't send password in a real app, but doing it for simplicity
            return ResponseEntity.ok(response);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        boolean updated = userService.updatePasswordByEmail(email, newPassword);
        Map<String, String> response = new HashMap<>();
        if (updated) {
            response.put("message", "Password reset successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        session.invalidate();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        Map<String, String> response = new HashMap<>();
        response.put("error", "Not logged in");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
