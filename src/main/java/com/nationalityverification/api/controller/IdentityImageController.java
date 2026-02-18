package com.nationalityverification.api.controller;

import com.nationalityverification.api.dto.response.PhotoUploadResponse;
import com.nationalityverification.application.service.IdentityImageService;
import com.nationalityverification.common.logging.LoggingUtils;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class IdentityImageController {

    private final IdentityImageService imageService;

    @PostMapping(value = "/api/v1/kimlik_kart_foto_on", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoUploadResponse> uploadFront(
            @RequestParam @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits") String tckn,
            @RequestParam("file") MultipartFile file) {

        log.info("Photo upload FRONT | tcknHmac={}", LoggingUtils.tcknHmac(tckn));
        String imageId = imageService.upload(tckn, "FRONT", file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PhotoUploadResponse(true, LoggingUtils.maskTckn(tckn), imageId, "FRONT"));
    }

    @PostMapping(value = "/api/v1/kimlik_kart_foto_arka", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoUploadResponse> uploadBack(
            @RequestParam @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits") String tckn,
            @RequestParam("file") MultipartFile file) {

        log.info("Photo upload BACK | tcknHmac={}", LoggingUtils.tcknHmac(tckn));
        String imageId = imageService.upload(tckn, "BACK", file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PhotoUploadResponse(true, LoggingUtils.maskTckn(tckn), imageId, "BACK"));
    }
}