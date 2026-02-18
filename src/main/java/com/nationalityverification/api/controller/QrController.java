package com.nationalityverification.api.controller;

import com.nationalityverification.application.port.in.GenerateQrUseCase;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Thin controller for QR code generation.
 * Delegates all business logic to {@link GenerateQrUseCase}.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class QrController {

    private static final Logger log = LoggerFactory.getLogger(QrController.class);

    private final GenerateQrUseCase generateQrUseCase;

    public QrController(GenerateQrUseCase generateQrUseCase) {
        this.generateQrUseCase = generateQrUseCase;
    }

    /**
     * GET /api/v1/qr?sessionId=...
     * Returns a PNG QR code image that encodes the Upsonic demo URL.
     */
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(
            @RequestParam("sessionId") @NotBlank(message = "sessionId must not be blank") String sessionId) {

        log.info("QR requested | sessionId=[{}]", sessionId);
        byte[] png = generateQrUseCase.generateQr(sessionId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }
}
