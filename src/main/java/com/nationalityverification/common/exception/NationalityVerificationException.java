package com.nationalityverification.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base runtime exception for the Nationality Verification domain.
 * Subclasses set a machine-readable {@code errorCode} and an appropriate {@link HttpStatus}.
 */
@Getter
public class NationalityVerificationException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public NationalityVerificationException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }

    public NationalityVerificationException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode  = errorCode;
        this.httpStatus = httpStatus;
    }
}
