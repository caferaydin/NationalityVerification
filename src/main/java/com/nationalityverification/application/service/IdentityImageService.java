package com.nationalityverification.application.service;

import com.nationalityverification.application.port.in.UploadIdentityImageUseCase;
import com.nationalityverification.common.logging.LoggingUtils;
import com.nationalityverification.domain.enums.ImageType;
import com.nationalityverification.domain.model.IdentityImage;
import com.nationalityverification.infrastructure.persistence.IdentityImageRepository;
import com.nationalityverification.infrastructure.storage.StoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Application service for uploading identity card images.
 */
@Service
public class IdentityImageService implements UploadIdentityImageUseCase {

    private static final Logger log = LoggerFactory.getLogger(IdentityImageService.class);

    private final StoragePort              storagePort;
    private final IdentityImageRepository  imageRepository;

    public IdentityImageService(StoragePort storagePort,
                                IdentityImageRepository imageRepository) {
        this.storagePort     = storagePort;
        this.imageRepository = imageRepository;
    }

    @Override
    public IdentityImage upload(String tckn, ImageType imageType, MultipartFile file) {
        // Use HMAC of TCKN as the storage sub-directory to avoid exposing PII in the filesystem
        String subDir = LoggingUtils.tcknHmac(tckn);

        log.info("Uploading {} image | tcknHmac={}", imageType, subDir);

        String storagePath = storagePort.store(subDir, file);

        IdentityImage image = new IdentityImage(tckn, imageType, storagePath);
        imageRepository.save(image);

        log.info("Image persisted | imageId={} | type={} | tcknHmac={}", image.getImageId(), imageType, subDir);
        return image;
    }
}
