package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.InvalidCredentialsException;
import com.jobwebsite.Exception.UserAlreadyExistsException;
import com.jobwebsite.Repository.UserRepository;
import com.jobwebsite.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        logger.info("Attempting to register user with username: {}", user.getUserName());

        validateUser(user); // Assuming this method checks for validity and throws exceptions if invalid

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    public User loginUser(String username, String password) throws InvalidCredentialsException {
        logger.info("Attempting login for username: {}", username);

        // Fetching the user by username
        User user = userRepository.findByUserName(username);
        if (user == null || !user.getPassword().equals(password)) {
            logger.error("Login failed for username: {}", username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        // Returning the user object upon successful login
        return user;
    }


    @Override
    public User updateUser(Long id, User user) throws UserAlreadyExistsException {
        logger.info("Attempting to update user with ID: {}", id);

        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + id)
        );

        if (!existingUser.getUserName().equals(user.getUserName()) && userRepository.findByUserName(user.getUserName()) != null) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (!existingUser.getEmailId().equals(user.getEmailId()) && userRepository.findByEmailId(user.getEmailId()) != null) {
            throw new UserAlreadyExistsException("Email ID already in use.");
        }

        existingUser.setUserName(user.getUserName());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmailId(user.getEmailId());
        existingUser.setPassword(user.getPassword());
        existingUser.setGender(user.getGender());
        existingUser.setMobileNo(user.getMobileNo());
        existingUser.setStatus(user.getStatus());
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
        if (userRepository.findByUserName(user.getUserName()) != null) {
            String message = String.format("Registration failed: Username '%s' is already taken.", user.getUserName());
            logger.error(message);
            throw new UserAlreadyExistsException(message);
        }

        if (userRepository.findByEmailId(user.getEmailId()) != null) {
            String message = String.format("Registration failed: Email ID '%s' is already in use.", user.getEmailId());
            logger.error(message);
            throw new UserAlreadyExistsException(message);
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            String message = "Registration failed: Password and Confirm Password do not match.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (!isValidPassword(user.getPassword())) {
            String message = "Registration failed: Password does not meet the required criteria.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        
        if (!isValidEmail(user.getEmailId())) {
            String message = "Registration failed: Invalid email format.";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6; // Example criteria: at least 6 characters long
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}