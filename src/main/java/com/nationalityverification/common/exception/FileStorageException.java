package com.nationalityverification.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a file storage operation fails.
 */
public class FileStorageException extends NationalityVerificationException {

    public FileStorageException(String message, Throwable cause) {
        super("FILE_STORAGE_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
