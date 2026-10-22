package com.expensesplitter.dto;

import java.time.LocalDateTime;

/**
 * Clean JSON error body returned by GlobalExceptionHandler.
 * { "timestamp": "...", "status": 404, "error": "Not Found", "message": "User not found with id 9" }
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
