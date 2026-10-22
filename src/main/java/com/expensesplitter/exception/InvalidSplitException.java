package com.expensesplitter.exception;

// Thrown when a split is wrong (percent != 100, exact total != amount, non-member, ...)
public class InvalidSplitException extends RuntimeException {

    public InvalidSplitException(String message) {
        super(message);
    }
}
