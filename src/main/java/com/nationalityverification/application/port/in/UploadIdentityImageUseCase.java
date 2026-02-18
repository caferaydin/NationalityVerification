package com.nationalityverification.application.port.in;

import com.nationalityverification.domain.enums.ImageType;
import com.nationalityverification.domain.model.IdentityImage;
import org.springframework.web.multipart.MultipartFile;

/**
 * Input port: handles uploading an identity card image (front or back).
 */
public interface UploadIdentityImageUseCase {

    /**
     * @param tckn      the citizen identification number
     * @param imageType FRONT or BACK
     * @param file      the multipart file to store
     * @return the persisted {@link IdentityImage} domain object
     */
    IdentityImage upload(String tckn, ImageType imageType, MultipartFile file);
}
