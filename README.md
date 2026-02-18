# Nationality Verification Service

Enterprise-grade Spring Boot 3.x / Java 17 service for identity verification via Upsonic integration.

## Architecture

```
com.nationalityverification
├── api                     # Controllers + DTOs (thin controllers only)
│   ├── advice              # GlobalExceptionHandler (@ControllerAdvice)
│   ├── controller          # QrController, WebhookController, IdentityImageController
│   └── dto
│       ├── request         # WebhookRequest, AnalyzedDataDto
│       └── response        # ImageUploadResponse, ErrorResponse
├── application             # Use-case interfaces + service implementations
│   ├── port.in             # GenerateQrUseCase, ProcessWebhookUseCase, UploadIdentityImageUseCase
│   └── service             # QrService, WebhookService, IdentityImageService
├── domain                  # Pure domain model (no framework dependencies)
│   ├── enums               # ImageType, VerificationStatus
│   └── model               # IdentityImage, IdentityVerificationResult
├── infrastructure          # Adapters for storage and persistence
│   ├── persistence         # Repository interfaces + in-memory implementations
│   └── storage             # StoragePort interface + LocalFileStorageAdapter
├── security                # HMAC validation, request body caching
│   ├── WebhookHmacValidator
│   └── CachedBodyRequestWrapper
└── common                  # Cross-cutting concerns
    ├── correlation         # CorrelationIdFilter (MDC + response header)
    ├── exception           # Domain exceptions hierarchy
    └── logging             # LoggingUtils (TCKN → HMAC for safe logging)
```

## Prerequisites

- Java 17+
- Maven Wrapper (`mvnw`) bundled in repo

## Running

```bash
# Set the webhook HMAC secret (or use the default "change-me-in-production" for dev)
$env:UPS_WEBHOOK_SECRET = "my-super-secret"

.\mvnw spring-boot:run
```

Server starts on port **8080**.

---

## Endpoints

### 1 — QR Code Generation

```
GET /api/v1/qr?sessionId={sessionId}
```

Returns a PNG image (300×300px) whose content is:
`https://demoqr.upsonic.ai/demo/qr/?sessionId={sessionId}`

**curl example:**
```bash
curl -o qr.png "http://localhost:8080/api/v1/qr?sessionId=abc-123"
```

---

### 2 — Upsonic Identity-Analysis Webhook

```
POST /api/v1/webhooks/identity-analysis
Content-Type: application/json
X-Signature: <hex(HMAC-SHA256(secret, timestamp + "." + rawBody))>
X-Timestamp: <unix-epoch-seconds>
```

**Request body:**
```json
{
  "tckn": 11111111111,
  "analyzed_data": {
    "verification_status": true,
    "verification_score": 0.95,
    "verification_description": "Identity verified successfully"
  }
}
```

**Response:** `200 OK` (empty body)

**curl example (generating signature in bash):**
```bash
SECRET="my-super-secret"
BODY='{"tckn":11111111111,"analyzed_data":{"verification_status":true,"verification_score":0.95,"verification_description":"ok"}}'
TS=$(date +%s)
SIG=$(printf '%s.%s' "$TS" "$BODY" | openssl dgst -sha256 -hmac "$SECRET" | awk '{print $2}')

curl -s -o /dev/null -w "%{http_code}" \
  -X POST http://localhost:8080/api/v1/webhooks/identity-analysis \
  -H "Content-Type: application/json" \
  -H "X-Timestamp: $TS" \
  -H "X-Signature: $SIG" \
  -d "$BODY"
```

---

### 3 — Identity Image Upload

#### Front of ID card
```
POST /api/v1/identity/{tckn}/images/front
Content-Type: multipart/form-data
```

**curl example:**
```bash
curl -s -X POST "http://localhost:8080/api/v1/identity/11111111111/images/front" \
  -F "file=@/path/to/front.jpg"
```

**Response `201 Created`:**
```json
{
  "imageId": "550e8400-e29b-41d4-a716-446655440000",
  "type": "FRONT"
}
```

#### Back of ID card
```
POST /api/v1/identity/{tckn}/images/back
Content-Type: multipart/form-data
```

```bash
curl -s -X POST "http://localhost:8080/api/v1/identity/11111111111/images/back" \
  -F "file=@/path/to/back.jpg"
```

**Response `201 Created`:**
```json
{
  "imageId": "660e8400-e29b-41d4-a716-446655440001",
  "type": "BACK"
}
```

---

## Error Response Format

All errors return a consistent JSON envelope:

```json
{
  "timestamp": "2026-02-18T10:00:00Z",
  "path": "/api/v1/webhooks/identity-analysis",
  "errorCode": "WEBHOOK_SIGNATURE_INVALID",
  "message": "Signature mismatch",
  "correlationId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

---

## Security Notes

- **TCKN is never written to logs** in raw form. All log lines reference `tcknHmac` — a truncated HMAC-SHA256 digest.
- Webhook requests require a valid HMAC-SHA256 signature with a ≤ 5-minute timestamp window.
- The `UPS_WEBHOOK_SECRET` environment variable **must** be set in production.
- Uploaded files are stored under `./data/uploads/<tcknHmac>/<uuid>.<ext>` — no PII in filesystem paths.

## Correlation ID

Every request receives a `X-Correlation-Id` response header. Supply your own via the request header to preserve end-to-end traceability.

## Running Tests

```bash
.\mvnw test
```

## Health Check

```bash
curl http://localhost:8080/actuator/health
```
