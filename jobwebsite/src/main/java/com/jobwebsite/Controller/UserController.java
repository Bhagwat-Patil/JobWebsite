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
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow specific origins for production use
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user.getUserName() == null || user.getPassword() == null || user.getEmailId() == null) {
                logger.error("Invalid registration details provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid registration details provided.");
            }

            User registeredUser = userService.registerUser(user);
            logger.info("User registered successfully with username: {}", user.getUserName());

            // Returning the registered user data in the response
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (UserAlreadyExistsException | IllegalArgumentException e) {
            logger.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed.");
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> loginUser(@RequestBody User user)  {
        try {
            if (user.getUserName().isEmpty() || user.getPassword().isEmpty()) {
                logger.error("Invalid login credentials provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid login credentials.");
            }
            userService.loginUser(user.getUserName(), user.getPassword());
            logger.info("User logged in successfully with username: {}", user.getUserName());
            return ResponseEntity.ok("Login successful.");
        } catch (InvalidCredentialsException e) {
            logger.error("User login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed.");
        }
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.updateUser(id, user);
            logger.info("User with ID: {} updated successfully.", id);
            return ResponseEntity.status(HttpStatus.OK).body("User updated successfully.");
        } catch (UserAlreadyExistsException e) {
            logger.error("User update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during user update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User update failed.");
        }
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            logger.info("User with ID: {} deleted successfully.", id);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
        } catch (Exception e) {
            logger.error("An unexpected error occurred during user deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User deletion failed.");
        }
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Fetched all users.");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while fetching the user", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getUserByStatus/{status}")
    public List<User> getUserByStatus(@PathVariable String status) {
        try {
            logger.info("Fetching Users with status: {}", status);
            return userService.getUserByStatus(status);
        } catch (Exception e) {
            logger.error("Error in fetching Users with status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

}