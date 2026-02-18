package com.nationalityverification.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown for domain-level business validation failures.
 */
public class DomainValidationException extends NationalityVerificationException {

    public DomainValidationException(String message) {
        super("DOMAIN_VALIDATION_ERROR", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
