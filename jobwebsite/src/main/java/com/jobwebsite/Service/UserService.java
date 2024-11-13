package com.jobwebsite.Service;

import com.jobwebsite.Entity.User;
import com.jobwebsite.Exception.InvalidCredentialsException;
import com.jobwebsite.Exception.UserAlreadyExistsException;

public interface UserService {
    User registerUser(User user) throws UserAlreadyExistsException;
    User loginUser(String username, String password) throws InvalidCredentialsException;
}
