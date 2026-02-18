package com.nationalityverification.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base checked exception for the Nationality Verification domain.
 */
public class NationalityVerificationException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public NationalityVerificationException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public NationalityVerificationException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode()    { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
