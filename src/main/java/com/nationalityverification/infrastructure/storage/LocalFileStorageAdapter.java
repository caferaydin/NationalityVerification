package com.nationalityverification.infrastructure.storage;

import com.nationalityverification.common.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Local filesystem implementation of {@link StoragePort}.
 * Files are stored under {@code ${uploads.baseDir}/<subDirectory>/<uuid>.<ext>}.
 *
 * <p>This class is a drop-in adapter; to switch to S3 provide an alternative
 * {@link StoragePort} bean and disable this one via a profile or condition.
 */
@Component
public class LocalFileStorageAdapter implements StoragePort {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageAdapter.class);

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

    @Override
    public String store(String subDirectory, MultipartFile file) {
        try {
            Path targetDir = baseDir.resolve(subDirectory);
            Files.createDirectories(targetDir);

            String extension = resolveExtension(file.getOriginalFilename());
            String fileName  = UUID.randomUUID() + extension;
            Path   targetPath = targetDir.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = baseDir.relativize(targetPath).toString();
            log.debug("Stored file to relative path: {}", relativePath);
            return relativePath;

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
