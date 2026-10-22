package com.expensesplitter.exception;

import com.expensesplitter.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @ControllerAdvice = one place that catches exceptions thrown by ANY controller
 * and turns them into a clean JSON error with the right HTTP status.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 - something with the given id does not exist
    @ExceptionHandler({UserNotFoundException.class, GroupNotFoundException.class, ExpenseNotFoundException.class,
            PaymentNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - split rules broken
    @ExceptionHandler(InvalidSplitException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSplit(InvalidSplitException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 409 - user cannot be deleted while expenses still refer to them
    @ExceptionHandler(UserInUseException.class)
    public ResponseEntity<ErrorResponse> handleUserInUse(UserInUseException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400 - bad input such as empty name or duplicate email
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadInput(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 - broken JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON");
    }

    // 400 - e.g. /api/groups/abc/balances (abc is not a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value '" + ex.getValue() + "' for " + ex.getName());
    }

    // 404 - URL does not match any endpoint, e.g. DELETE /api/expenses/ (id missing)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoEndpoint(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "No endpoint at /" + ex.getResourcePath() + " - check the URL and the id");
    }

    // 405 - right URL, wrong HTTP method (e.g. GET /api/expenses/1)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongMethod(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMethod() + " is not supported here. Allowed: "
                + ex.getSupportedHttpMethods());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return new ResponseEntity<>(body, status);
    }
}
