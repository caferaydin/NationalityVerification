package com.nationalityverification.api.controller;

import com.nationalityverification.api.dto.response.ImageUploadResponse;
import com.nationalityverification.application.port.in.UploadIdentityImageUseCase;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.domain.enums.ImageType;
import com.nationalityverification.domain.model.IdentityImage;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Thin controller for identity card image uploads.
 * Delegates storage and persistence to {@link UploadIdentityImageUseCase}.
 */
@RestController
@RequestMapping("/api/v1/identity")
@Validated
public class IdentityImageController {

    private static final Logger log = LoggerFactory.getLogger(IdentityImageController.class);

    private final UploadIdentityImageUseCase uploadUseCase;

    public IdentityImageController(UploadIdentityImageUseCase uploadUseCase) {
        this.uploadUseCase = uploadUseCase;
    }

    /**
     * POST /api/v1/identity/{tckn}/images/front
     */
    @PostMapping(value = "/{tckn}/images/front",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadFront(
            @PathVariable @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits") String tckn,
            @RequestParam("file") MultipartFile file) {

        log.info("Front image upload | tcknHmac={}", LoggingUtils.tcknHmac(tckn));
        IdentityImage image = uploadUseCase.upload(tckn, ImageType.FRONT, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ImageUploadResponse(image.getImageId(), image.getImageType().name()));
    }

    /**
     * POST /api/v1/identity/{tckn}/images/back
     */
    @PostMapping(value = "/{tckn}/images/back",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadBack(
            @PathVariable @Pattern(regexp = "\\d{11}", message = "tckn must be exactly 11 digits") String tckn,
            @RequestParam("file") MultipartFile file) {

        log.info("Back image upload | tcknHmac={}", LoggingUtils.tcknHmac(tckn));
        IdentityImage image = uploadUseCase.upload(tckn, ImageType.BACK, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ImageUploadResponse(image.getImageId(), image.getImageType().name()));
    }
}
