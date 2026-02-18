# Nationality Verification Service

Spring Boot 3.x / Java 17 PoC — kimlik doğrulama ve fotoğraf yükleme servisi.

## Paket Yapısı

```
com.nationalityverification
├── api
│   ├── advice              # GlobalExceptionHandler
│   ├── controller          # QrController, WebhookController, IdentityImageController
│   └── dto
│       ├── request         # WebhookRequest, AnalyzedDataDto, TcknDeserializer
│       └── response        # PhotoUploadResponse, ErrorResponse
├── application
│   └── service             # QrService, WebhookService, IdentityImageService
├── infrastructure
│   └── storage             # LocalFileStorageAdapter
└── common
    ├── correlation         # CorrelationIdFilter
    ├── exception           # NationalityVerificationException, FileStorageException
    └── logging             # LoggingUtils
```

## Çalıştırma

```bash
.\mvnw spring-boot:run
```

Port: **8080**

---

## Endpoint'ler

### 1 — QR Kodu

```
GET /api/v1/qr?sessionId={sessionId}
```

PNG döner, içeriği: `https://demoqr.upsonic.ai/demo/qr/{sessionId}`

```bash
curl -o qr.png "http://localhost:8080/api/v1/qr?sessionId=abc-123"
```

---

### 2 — Kimlik Kartı Analizi

```
POST /api/v1/kimlik_kart_analizi
Content-Type: application/json
```

```json
{
  "tckn": 11111111111,
  "analyzed_data": {
    "verification_status": true,
    "verification_score": 0.95,
    "verification_description": "Identity verified"
  }
}
```

**Response:** `200 OK` (boş body)

```bash
curl -s -X POST http://localhost:8080/api/v1/kimlik_kart_analizi \
  -H "Content-Type: application/json" \
  -d '{"tckn":11111111111,"analyzed_data":{"verification_status":true,"verification_score":0.95,"verification_description":"ok"}}'
```

Validasyon: `tckn` 11 hane, `verification_score` 0.0–1.0

---

### 3 — Kimlik Fotoğrafı Yükle

#### Ön yüz
```
POST /api/v1/kimlik_kart_foto_on?tckn={tckn}
Content-Type: multipart/form-data
```

```bash
curl -s -X POST "http://localhost:8080/api/v1/kimlik_kart_foto_on?tckn=11111111111" \
  -F "file=@/path/to/front.jpg"
```

#### Arka yüz
```
POST /api/v1/kimlik_kart_foto_arka?tckn={tckn}
Content-Type: multipart/form-data
```

```bash
curl -s -X POST "http://localhost:8080/api/v1/kimlik_kart_foto_arka?tckn=11111111111" \
  -F "file=@/path/to/back.jpg"
```

**Response `201 Created`:**
```json
{
  "success": true,
  "tckn": "111******11",
  "imageId": "550e8400-e29b-41d4-a716-446655440000",
  "side": "FRONT"
}
```

- Kabul edilen dosya tipleri: `image/jpeg`, `image/jpg`, `image/png`
- Dosya max boyutu: 10 MB (request: 20 MB)
- Depolama: `./data/uploads/<tcknHmac>/<uuid>.<ext>`
- `tckn` response'ta maskelenir; raw TCKN loglanmaz

---

## Hata Zarfı

```json
{
  "timestamp": "2026-02-19T10:00:00Z",
  "path": "/endpoint/kimlik_kart_analizi",
  "errorCode": "VALIDATION_ERROR",
  "message": "tckn: tckn must be exactly 11 digits",
  "correlationId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

## Cross-cutting

- `X-Correlation-Id`: her request'e MDC + response header olarak eklenir
- TCKN loglarda hiçbir zaman raw gösterilmez (`tcknHmac` kullanılır)

## Testler

```bash
.\mvnw test
```

## Health

```bash
curl http://localhost:8080/actuator/health
```


