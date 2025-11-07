package io.yugurt.booking_platform.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomError e) {
        log.warn("Business exception occurred: {}", e.getMessage());

        var errorResponse = new ErrorResponse(
            e.getMessage(),
            e.getErrorCode().name(),
            Instant.now()
        );

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation exception occurred: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        var errorResponse = new ValidationErrorResponse(
            "입력값 검증에 실패했습니다.",
            "VALIDATION_FAILED",
            errors,
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception occurred", e);

        var errorResponse = new ErrorResponse(
            "서버 오류가 발생했습니다.",
            "INTERNAL_SERVER_ERROR",
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public record ErrorResponse(
        String message,
        String errorCode,
        Instant timestamp
    ) {

    }

    public record ValidationErrorResponse(
        String message,
        String errorCode,
        Map<String, String> fieldErrors,
        Instant timestamp
    ) {

    }
}


