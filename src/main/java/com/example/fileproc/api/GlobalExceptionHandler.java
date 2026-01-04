package com.example.fileproc.api;

import com.example.fileproc.application.FileParseException;
import com.example.fileproc.application.FileValidationException;
import com.example.fileproc.infrastructure.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponse> handleFileValidationException(FileValidationException ex,
                                                                        HttpServletRequest request) {
        String requestId = getRequestIdString(request);
        ErrorResponse response = new ErrorResponse(
                requestId,
                "BAD_REQUEST",
                "File validation failed",
                ex.getErrors()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileParseException.class)
    public ResponseEntity<ErrorResponse> handleFileParseException(FileParseException ex,
                                                                   HttpServletRequest request) {
        String requestId = getRequestIdString(request);
        ErrorResponse response = new ErrorResponse(
                requestId,
                "BAD_REQUEST",
                "File parse error",
                List.of(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                 HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        String requestId = getRequestIdString(request);
        ErrorResponse response = new ErrorResponse(
                requestId,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getRequestIdString(HttpServletRequest request) {
        UUID requestId = RequestIdFilter.getRequestId(request);
        return requestId != null ? requestId.toString() : null;
    }
}
