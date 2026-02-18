package com.nationalityverification.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Port (interface) for binary file storage.
 * Implementations may be local filesystem, S3, Azure Blob, etc.
 */
public interface StoragePort {

    /**
     * Persists the uploaded file under the given sub-directory.
     *
     * @param subDirectory logical sub-directory (e.g. tckn-hmac token)
     * @param file         the uploaded multipart file
     * @return relative storage path of the saved file
     */
    String store(String subDirectory, MultipartFile file);
}
