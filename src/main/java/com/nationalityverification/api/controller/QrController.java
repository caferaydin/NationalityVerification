package com.nationalityverification.api.controller;

import com.nationalityverification.application.service.QrService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Thin controller for QR code generation.
 * Delegates all business logic to {@link GenerateQrUseCase}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class QrController {

    private final QrService generateQrUseCase;

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
