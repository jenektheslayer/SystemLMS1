package org.example.exception;

public class CourseHasScheduleException extends RuntimeException {
    public CourseHasScheduleException(String message) {
        super(message);
    }
}
