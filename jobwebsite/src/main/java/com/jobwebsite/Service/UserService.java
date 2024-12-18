package com.jobwebsite.Service;

import com.jobwebsite.Entity.Internship;
import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.InvalidCredentialsException;
import com.jobwebsite.Exception.UserAlreadyExistsException;
import com.jobwebsite.Exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    User registerUser(User user) throws UserAlreadyExistsException;
    User loginUser(String username, String password) throws InvalidCredentialsException;
    User updateUser(Long id, User user) throws UserAlreadyExistsException;
    void deleteUser(Long id);
    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> getUserByStatus(String status);

}
