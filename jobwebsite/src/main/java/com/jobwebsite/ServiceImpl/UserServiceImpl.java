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

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        logger.info("Attempting to register user with username: {}", user.getUserName());

        validateUser(user);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    public User loginUser(String username, String password) throws InvalidCredentialsException {
        logger.info("Attempting login for username: {}", username);

        User user = userRepository.findByUserName(username);
        if (user == null || !user.getPassword().equals(password)) {
            logger.error("Login failed for username: {}", username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        return user;
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