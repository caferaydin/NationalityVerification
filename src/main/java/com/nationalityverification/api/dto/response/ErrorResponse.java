package com.nationalityverification.api.dto.response;

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
 */
public class ErrorResponse {

    private String timestamp;
    private String path;
    private String errorCode;
    private String message;
    private String correlationId;

    public ErrorResponse() {}

    public ErrorResponse(String timestamp, String path, String errorCode,
                         String message, String correlationId) {
        this.timestamp     = timestamp;
        this.path          = path;
        this.errorCode     = errorCode;
        this.message       = message;
        this.correlationId = correlationId;
    }

    public String getTimestamp()     { return timestamp; }
    public String getPath()          { return path; }
    public String getErrorCode()     { return errorCode; }
    public String getMessage()       { return message; }
    public String getCorrelationId() { return correlationId; }

    public void setTimestamp(String t)     { this.timestamp = t; }
    public void setPath(String p)          { this.path = p; }
    public void setErrorCode(String c)     { this.errorCode = c; }
    public void setMessage(String m)       { this.message = m; }
    public void setCorrelationId(String c) { this.correlationId = c; }
}
