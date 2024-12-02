package com.jobwebsite.Exception;

public class AdminNotEnabledException extends RuntimeException {
    public AdminNotEnabledException(String message) {
        super(message);
    }
}