package com.jobwebsite.Exception;

public class AdminNotApprovedException extends RuntimeException {
    public AdminNotApprovedException(String message) {
        super(message);
    }
}