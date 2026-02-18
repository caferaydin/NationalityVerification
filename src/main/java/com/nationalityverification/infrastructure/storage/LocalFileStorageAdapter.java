package com.nationalityverification.infrastructure.storage;

import com.nationalityverification.common.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Local filesystem implementation of {@link StoragePort}.
 * Files are stored under {@code ${uploads.baseDir}/\u003csubDirectory\u003e/\u003cuuid\u003e.\u003cext\u003e}.
 *
 * <p>This class is a drop-in adapter; to switch to S3 provide an alternative
 * {@link StoragePort} bean and disable this one via a profile or condition.
 */
@Slf4j
@Component
public class LocalFileStorageAdapter {
    private final Path baseDir;

    public LocalFileStorageAdapter(@Value("${uploads.baseDir:./data/uploads}") String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDir);
            log.info("Local file storage initialised at {}", this.baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload directory: " + this.baseDir, e);
        }
    }

    public String storeAndReturnId(String subDirectory, MultipartFile file) {
        try {
            Path targetDir = baseDir.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String extension  = resolveExtension(file.getOriginalFilename());
            String imageId    = UUID.randomUUID().toString();
            Path   targetPath = targetDir.resolve(imageId + extension);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Stored file | imageId={} | path={}", imageId, baseDir.relativize(targetPath));
            return imageId;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file for directory: " + subDirectory, e);
        }
    }

    private String resolveExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return "";
    }
}
