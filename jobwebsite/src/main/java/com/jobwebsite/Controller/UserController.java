package com.jobwebsite.Controller;

import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.InvalidCredentialsException;
import com.jobwebsite.Exception.UserAlreadyExistsException;
import com.jobwebsite.Service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow specific origins for production use
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            if (user.getUserName() == null || user.getPassword() == null || user.getEmailId() == null) {
                logger.error("Invalid registration details provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid registration details provided.");
            }
            
            userService.registerUser(user);
            logger.info("User registered successfully with username: {}", user.getUserName());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (UserAlreadyExistsException | IllegalArgumentException e) {
            logger.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password) {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                logger.error("Invalid login credentials provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid login credentials.");
            }
            
            userService.loginUser(username, password);
            logger.info("User logged in successfully with username: {}", username);
            return ResponseEntity.ok("Login successful.");
        } catch (InvalidCredentialsException e) {
            logger.error("User login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed.");
        }
    }
}