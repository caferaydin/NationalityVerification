package com.nationalityverification.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends NationalityVerificationException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super("RESOURCE_NOT_FOUND",
              resourceType + " not found: " + identifier,
              HttpStatus.NOT_FOUND);
    }
}
