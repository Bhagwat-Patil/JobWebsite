package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.UserAlreadyExistsException;
import com.jobwebsite.Repository.ForgotPasswordOtpRepository;
import com.jobwebsite.Repository.UserRepository;
import com.jobwebsite.Service.EmailService;
import com.jobwebsite.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        logger.info("Registering user: {}", user.getEmailId());

        // Fetch existing user by email ID
        User existingUser = userRepository.findByEmailId(user.getEmailId());

        if (existingUser != null) {
            throw new RuntimeException("User with this email ID already exists.");
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Clear confirm password for security reasons
        user.setConfirmPassword(null);

        // Set default status
        user.setStatus("ACTIVE");

        // Save the user to the repository
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String username, String password) {
        logger.info("Attempting login for user: {}", username);

        // Fetch the user by username
        User user = userRepository.findByUserName(username);

        // Validate user existence
        if (user == null) {
            logger.error("Login failed for username: {} - User not found", username);
            throw new RuntimeException("Invalid username or password.");
        }

        // Validate the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Login failed for username: {} - Invalid password", username);
            throw new RuntimeException("Invalid username or password.");
        }

        logger.info("Login successful for username: {}", username);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) throws UserAlreadyExistsException {
        logger.info("Attempting to update user with ID: {}", id);

        // Fetch the existing user
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + id)
        );

        // Validate uniqueness of username if it is being changed
        if (!existingUser.getUserName().equals(user.getUserName()) && userRepository.findByUserName(user.getUserName()) != null) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        // Validate uniqueness of email if it is being changed
        if (!existingUser.getEmailId().equals(user.getEmailId()) && userRepository.findByEmailId(user.getEmailId()) != null) {
            throw new UserAlreadyExistsException("Email ID already in use.");
        }

        // Update fields
        existingUser.setUserName(user.getUserName());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmailId(user.getEmailId());
        existingUser.setGender(user.getGender());
        existingUser.setMobileNo(user.getMobileNo());
        existingUser.setStatus(user.getStatus());

        // Encrypt and update the password only if it is being changed
        if (!user.getPassword().isEmpty() && !user.getPassword().equals(existingUser.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(encryptedPassword);
        }

        // Save the updated user to the database
        User updatedUser = userRepository.save(existingUser);
        logger.info("User with ID: {} updated successfully.", updatedUser.getId());

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);

        // Check if the user exists
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + id)
        );

        userRepository.deleteById(id);
        logger.info("User with ID: {} deleted successfully.", id);
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Fetching all users.");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + id)
        );
    }

    @Override
    public List<User> getUserByStatus(String status) {
        try {
            logger.info("Fetching Users with status: {}", status);
            return userRepository.findByStatus(status);
        } catch (Exception e) {
            logger.error("Error while fetching Users with status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    private void validateUser(User user) throws UserAlreadyExistsException {
        // Check if the username is already taken
        if (userRepository.findByUserName(user.getUserName()) != null) {
            String message = String.format("Registration failed: Username '%s' is already taken.", user.getUserName());
            logger.error(message);
            throw new UserAlreadyExistsException(message);
        }

        // Check if the email ID is already in use
        if (userRepository.findByEmailId(user.getEmailId()) != null) {
            String message = String.format("Registration failed: Email ID '%s' is already in use.", user.getEmailId());
            logger.error(message);
            throw new UserAlreadyExistsException(message);
        }

        // Check if the password and confirm password match
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            String message = "Registration failed: Password and Confirm Password do not match.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        // Validate the password format
        if (!isValidPassword(user.getPassword())) {
            String message = "Registration failed: Password does not meet the required criteria.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        // Validate the email format
        if (!isValidEmail(user.getEmailId())) {
            String message = "Registration failed: Invalid email format.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        // Encrypt the password before saving (integrating PasswordEncoder)
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setConfirmPassword(encryptedPassword); // Optional, depending on your application's requirements
    }

    // Example password validation method
    private boolean isValidPassword(String password) {
        // Example criteria: at least 8 characters, one uppercase, one lowercase, one digit, one special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        return password.matches(passwordPattern);
    }

    // Example email validation method
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailPattern);
    }

}