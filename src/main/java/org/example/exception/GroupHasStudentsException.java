package org.example.exception;

public class GroupHasStudentsException extends RuntimeException {
    public GroupHasStudentsException(String message) {
        super(message);
    }
}
