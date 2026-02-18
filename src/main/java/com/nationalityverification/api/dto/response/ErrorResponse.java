package com.nationalityverification.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Standardised error response envelope returned by the {@code GlobalExceptionHandler}.
 * <pre>
 * {
 *   "timestamp": "2026-02-18T10:00:00Z",
 *   "path": "/api/v1/...",
 *   "errorCode": "WEBHOOK_SIGNATURE_INVALID",
 *   "message": "...",
 *   "correlationId": "..."
 * }
 * </pre>
 *
 * <p>{@code @NoArgsConstructor} and {@code @AllArgsConstructor} keep Jackson
 * deserialisation and {@code GlobalExceptionHandler}'s {@code buildError()} working.
 * {@code @Getter}/{@code @Setter} replace all hand-written accessors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;
    private String path;
    private String errorCode;
    private String message;
    private String correlationId;
}
