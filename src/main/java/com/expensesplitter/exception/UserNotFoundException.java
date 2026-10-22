package com.expensesplitter.exception;

// Unchecked (RuntimeException) so it also triggers @Transactional rollback automatically
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found with id " + id);
    }
}
