package com.expensesplitter.exception;

// Thrown when deleting a user who still appears in expenses (mapped to 409 Conflict)
public class UserInUseException extends RuntimeException {

    public UserInUseException(String message) {
        super(message);
    }
}
