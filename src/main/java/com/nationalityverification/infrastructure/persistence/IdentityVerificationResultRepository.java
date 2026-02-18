package com.nationalityverification.infrastructure.persistence;

import com.nationalityverification.domain.model.IdentityVerificationResult;

import java.util.Optional;

/**
 * Repository port for {@link IdentityVerificationResult}.
 */
public interface IdentityVerificationResultRepository {

    IdentityVerificationResult save(IdentityVerificationResult result);

    Optional<IdentityVerificationResult> findLatestByTckn(String tckn);
}
