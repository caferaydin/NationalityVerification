package com.nationalityverification.application.service;

import com.nationalityverification.common.exception.NationalityVerificationException;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.infrastructure.storage.LocalFileStorageAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");

    private final LocalFileStorageAdapter storage;

    public String upload(String tckn, String side, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new NationalityVerificationException(
                    "INVALID_FILE_TYPE",
                    "Only JPG and PNG files are accepted. Got: " + contentType,
                    HttpStatus.BAD_REQUEST);
        }
        String subDir   = LoggingUtils.tcknHmac(tckn);
        String imageId  = storage.storeAndReturnId(subDir, file);
        log.info("File stored | side={} | imageId={} | tcknHmac={}", side, imageId, subDir);
        return imageId;
    }
}
