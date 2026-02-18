package com.nationalityverification.api.dto.response;

/**
 * Response DTO returned after a successful identity image upload.
 */
public class ImageUploadResponse {

    private final String imageId;
    private final String type;

    public ImageUploadResponse(String imageId, String type) {
        this.imageId = imageId;
        this.type    = type;
    }

    public String getImageId() { return imageId; }
    public String getType()    { return type; }
}
