package io.yugurt.booking_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomError e) {
        var errorResponse = new ErrorResponse(
            e.getMessage(),
            e.getErrorCode().name()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        var errorResponse = new ErrorResponse(
            e.getMessage(),
            "INTERNAL_SERVER_ERROR"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    public record ErrorResponse(
        String message,
        String errorCode
    ) {
    }
}
