package com.nationalityverification.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the HMAC signature on an incoming webhook is invalid or the timestamp is stale.
 */
public class WebhookSignatureException extends NationalityVerificationException {

    public WebhookSignatureException(String message) {
        super("WEBHOOK_SIGNATURE_INVALID", message, HttpStatus.UNAUTHORIZED);
    }
}
