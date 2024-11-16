package com.jobwebsite.Exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {

        super("Job not found with id: " + id);
    }

    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
