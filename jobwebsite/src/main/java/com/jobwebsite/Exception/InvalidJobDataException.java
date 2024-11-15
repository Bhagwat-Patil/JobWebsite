package com.jobwebsite.Exception;

public class InvalidJobDataException extends Throwable {
    public InvalidJobDataException(String jobTitleCannotBeEmpty) {
        super(jobTitleCannotBeEmpty);
    }
}
