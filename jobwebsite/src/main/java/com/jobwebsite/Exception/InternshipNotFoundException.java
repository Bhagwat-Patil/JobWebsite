package com.jobwebsite.Exception;

public class InternshipNotFoundException extends RuntimeException {
    public InternshipNotFoundException(String message) {
        super(message);
    }
}