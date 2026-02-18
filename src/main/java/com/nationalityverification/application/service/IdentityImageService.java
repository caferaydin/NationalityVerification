package com.nationalityverification.application.service;

import com.nationalityverification.application.store.VerificationStore;
import com.nationalityverification.common.exception.NationalityVerificationException;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.infrastructure.storage.LocalFileStorageAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");

    private final LocalFileStorageAdapter storage;
    private final VerificationStore        verificationStore;

    @Value("${verification.ttl-minutes:3}")
    private int ttlMinutes;

    public String upload(String tckn, String side, MultipartFile file) {
        validateVerification(tckn);
        validateFileType(file);

        String subDir  = LoggingUtils.tcknHmac(tckn);
        String imageId = storage.storeAndReturnId(subDir, file);
        log.info("File stored | side={} | imageId={} | tcknHmac={}", side, imageId, subDir);
        return imageId;
    }

    private void validateVerification(String tckn) {
        VerificationStore.Entry entry = verificationStore.get(tckn);

        if (entry == null) {
            throw new NationalityVerificationException(
                    "VERIFICATION_NOT_FOUND",
                    "No verification result found for this TCKN. Complete identity scan first.",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!entry.status()) {
            throw new NationalityVerificationException(
                    "VERIFICATION_FAILED",
                    "Identity verification was not successful for this TCKN.",
                    HttpStatus.FORBIDDEN);
        }
        long minutesAgo = (Instant.now().getEpochSecond() - entry.receivedAt().getEpochSecond()) / 60;
        if (minutesAgo > ttlMinutes) {
            throw new NationalityVerificationException(
                    "VERIFICATION_EXPIRED",
                    "Verification result is older than " + ttlMinutes + " minutes. Please re-scan.",
                    HttpStatus.GONE);
        }
        log.info("Verification valid | tcknHmac={} | age={}min", LoggingUtils.tcknHmac(tckn), minutesAgo);
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new NationalityVerificationException(
                    "INVALID_FILE_TYPE",
                    "Only JPG and PNG files are accepted. Got: " + contentType,
                    HttpStatus.BAD_REQUEST);
        }
    }
}
