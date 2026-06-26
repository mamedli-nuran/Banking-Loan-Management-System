package com.example.msloan.handler;

import com.example.msloan.dto.error.ErrorMessage;
import com.example.msloan.exception.CustomerNotFoundException;
import com.example.msloan.exception.LoanNotFoundException;
import com.example.msloan.exception.LoanStatusException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({LoanNotFoundException.class, CustomerNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFound(RuntimeException e, HttpServletRequest request) {
        log.warn(e.getMessage());

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(LoanStatusException.class)
    public ResponseEntity<ErrorMessage> handleLoanStatus(LoanStatusException e, HttpServletRequest request) {
        log.warn("Loan status conflict at [{}]: {}", request.getRequestURI(), e.getMessage());

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleConflict(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("Database constraint violation at {}: {}", request.getRequestURI(), e.getMessage(), e);

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message("Loan data violates a database constraint")
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        log.warn("Validation failed for path [{}]. Fields violated: {}", request.getRequestURI(), validationErrors.size());

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message("Validation failed for one or more fields")
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleUnreadableBody(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("Malformed or missing request body at [{}]: {}", request.getRequestURI(), e.getMessage());

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message("Request body is required or malformed")
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred while processing request [{}]: {}", request.getRequestURI(), e.getMessage(), e);

        ErrorMessage error = ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .message("An unexpected error occurred by Nuran Team. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}