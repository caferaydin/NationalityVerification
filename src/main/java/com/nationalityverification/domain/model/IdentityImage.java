package com.nationalityverification.domain.model;

import com.nationalityverification.domain.enums.ImageType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing a stored identity image (front or back of ID card).
 * Persistence is delegated to the infrastructure layer; this is a pure domain object.
 */
public class IdentityImage {

    private final String imageId;
    private final String tckn;
    private final ImageType imageType;
    private final String storagePath;
    private final Instant uploadedAt;

    public IdentityImage(String tckn, ImageType imageType, String storagePath) {
        this.imageId = UUID.randomUUID().toString();
        this.tckn = tckn;
        this.imageType = imageType;
        this.storagePath = storagePath;
        this.uploadedAt = Instant.now();
    }

    // Used when reconstituting from storage
    public IdentityImage(String imageId, String tckn, ImageType imageType,
                         String storagePath, Instant uploadedAt) {
        this.imageId = imageId;
        this.tckn = tckn;
        this.imageType = imageType;
        this.storagePath = storagePath;
        this.uploadedAt = uploadedAt;
    }

    public String getImageId()      { return imageId; }
    public String getTckn()         { return tckn; }
    public ImageType getImageType() { return imageType; }
    public String getStoragePath()  { return storagePath; }
    public Instant getUploadedAt()  { return uploadedAt; }
}
