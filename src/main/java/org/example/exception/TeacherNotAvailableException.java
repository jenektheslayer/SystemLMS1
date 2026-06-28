package org.example.exception;

public class TeacherNotAvailableException extends RuntimeException {
    public TeacherNotAvailableException(String message) {
        super(message);
    }
}
