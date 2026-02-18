package com.nationalityverification.api.advice;

import com.nationalityverification.api.dto.response.ErrorResponse;
import com.nationalityverification.common.correlation.CorrelationIdFilter;
import com.nationalityverification.common.exception.NationalityVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Centralised exception-to-response mapping.
 * Produces a consistent {@link ErrorResponse} envelope for all error cases.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // ------------------------------------------------------------------ domain exceptions

    @ExceptionHandler(NationalityVerificationException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            NationalityVerificationException ex, HttpServletRequest request) {

        log.warn("Domain exception | errorCode={} | path={} | message={}",
                 ex.getErrorCode(), request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(buildError(ex.getErrorCode(), ex.getMessage(), request));
    }

    // ------------------------------------------------------------------ validation

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failure | path={} | details={}", request.getRequestURI(), details);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("VALIDATION_ERROR", details, request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.warn("Constraint violation | path={} | message={}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("CONSTRAINT_VIOLATION", ex.getMessage(), request));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("MISSING_PARAMETER", ex.getMessage(), request));
    }

    @ExceptionHandler(org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
            org.springframework.web.multipart.support.MissingServletRequestPartException ex,
            HttpServletRequest request) {

        log.warn("Missing multipart part | path={} | part={}", request.getRequestURI(), ex.getRequestPartName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("MISSING_FILE_PART", "Required multipart field '" + ex.getRequestPartName() + "' is not present", request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("MALFORMED_REQUEST_BODY", "Request body could not be parsed", request));
    }

    // ------------------------------------------------------------------ catch-all

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception | path={}", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("INTERNAL_ERROR", "An unexpected error occurred", request));
    }

    // ------------------------------------------------------------------ helpers

    private ErrorResponse buildError(String errorCode, String message, HttpServletRequest request) {
        return new ErrorResponse(
            Instant.now().toString(),
            request.getRequestURI(),
            errorCode,
            message,
            MDC.get(CorrelationIdFilter.MDC_CORRELATION_KEY)
        );
    }
}
