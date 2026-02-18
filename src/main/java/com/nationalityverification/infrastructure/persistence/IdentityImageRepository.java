package com.nationalityverification.infrastructure.persistence;

import com.nationalityverification.domain.model.IdentityImage;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for {@link IdentityImage}.
 * Currently backed by an in-memory store; replace with JPA implementation
 * when a database is introduced.
 */
public interface IdentityImageRepository {

    IdentityImage save(IdentityImage image);

    Optional<IdentityImage> findById(String imageId);

    List<IdentityImage> findByTckn(String tckn);
}
