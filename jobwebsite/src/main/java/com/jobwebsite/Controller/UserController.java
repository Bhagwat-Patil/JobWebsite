package com.jobwebsite.Controller;

import com.jobwebsite.Entity.PasswordResetRequest;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.UserAlreadyExistsException;
import com.jobwebsite.Exception.UserNotFoundException;
import com.jobwebsite.Repository.ForgotPasswordOtpRepository;
import com.jobwebsite.Service.EmailService;
import com.jobwebsite.Service.UserService;
import com.jobwebsite.ServiceImpl.ForgotPasswordService;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            logger.info("Register endpoint called.");
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException ex) {
            logger.error("Error during registration: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user) {
        logger.info("Login request received for username: {}", user.getUserName());
        try {
            User loggedInUser = userService.loginUser(user.getUserName(), user.getPassword());
            return ResponseEntity.ok(loggedInUser);
        } catch (RuntimeException e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

    //********************** Forgot Password ********************************

    @PostMapping("/user/forgotPassword/{userEmail}")
    public ResponseEntity<String> forgotPassword(@PathVariable String userEmail) {
        logger.info("Received the request to reset password for email: {}", userEmail);
        try {
            // Step 1: Request OTP for password reset
            forgotPasswordService.requestPasswordReset(userEmail); // OTP will be sent internally
            return ResponseEntity.ok("Password reset OTP has been sent to your email.");
        } catch (UserNotFoundException e) {
            logger.error("User not found with email: {}", userEmail);
            return ResponseEntity.status(404).body("User not found with email: " + userEmail);
        } catch (Exception e) {
            logger.error("An error occurred while processing the password reset request for email: {}", userEmail, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/user/resetPassword/{userEmail}")
    public ResponseEntity<String> resetPassword(@PathVariable("userEmail") String userEmail,
                                                @RequestBody PasswordResetRequest request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String otp = request.getOtp();

        logger.info("Received request to reset password for email: {}", userEmail);

        // Step 1: Check if passwords match
        if (!password.equals(confirmPassword)) {
            logger.error("Passwords do not match for email: {}", userEmail);
            return ResponseEntity.status(400).body("Passwords do not match");
        }

        try {
            // Step 2: Verify OTP and reset password in a single transaction
            boolean isOtpValid = forgotPasswordService.verifyOtp(otp, userEmail);

            if (!isOtpValid) {
                logger.error("Invalid or expired OTP: {} for email: {}", otp, userEmail);
                return ResponseEntity.status(400).body("Invalid or expired OTP");
            }

            forgotPasswordService.resetPassword(userEmail, password);
            logger.info("Password successfully reset for email: {}", userEmail);
            return ResponseEntity.ok("Password successfully reset");
        } catch (UserNotFoundException e) {
            logger.error("No account found for email: {}", userEmail, e);
            return ResponseEntity.status(404).body("No account found for email: " + userEmail);
        } catch (Exception e) {
            logger.error("An error occurred while resetting password for email: {}", userEmail, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

}